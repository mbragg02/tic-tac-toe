package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.dl.GameRepository;
import com.mbragg.game.service.api.dto.*;
import com.mbragg.game.service.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Service
public class GameServiceImpl implements GameService {

    private static final int MINIMUM_PLAYERS = 2;
    private static final int MINIMUM_BOARD_SIZE = 3;

    private static Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    private final GameRepository gameRepository;

    private final UserService userService;

    private final Random random;

    public GameServiceImpl(GameRepository gameRepository, UserService userService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.random = new Random();
    }

    @Override
    public GameResponse create(CreateGameRequest gameRequest) {
        notNull(gameRequest, "Create game request can not be null");

        notNull(gameRequest.getUserIds(), "New Games require two users.");

        List<Long> userIds = gameRequest.getUserIds();
        isTrue(userIds.size() == MINIMUM_PLAYERS, "New Games require two users.");

        User userOne = userService.findOne(userIds.get(0));
        User userTwo = userService.findOne(userIds.get(1));

        notNull(userOne, "User one must exist.");
        notNull(userTwo, "User two must exist.");

        isTrue(gameRequest.getBoardSize() >= MINIMUM_BOARD_SIZE, "The minimum board size is 3.");
        Board board = new Board(gameRequest.getBoardSize());

        Player playerOne = new Player(userOne);
        Player playerTwo = new Player(userTwo);

        assignMark(playerOne, playerTwo);
        assignTurn(playerOne, playerTwo);

        playerOne.setStatus(PlayerStatus.PLAYING);
        playerTwo.setStatus(PlayerStatus.PLAYING);

        Game game = new Game();
        game.setBoard(board);
        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);
        game.setStatus(GameStatus.IN_PROGRESS);

        Game savedGame = gameRepository.save(game);

        logger.debug("Game {} saved successfully", savedGame.getId());

        return getGameResponse(savedGame);
    }

    /**
     * Assign the initial 'turn' to be either player one or player two at random.
     *
     * @param playerOne First player
     * @param playerTwo Second player
     */
    private void assignTurn(Player playerOne, Player playerTwo) {
        if (random.nextBoolean()) {
            playerOne.setTurn(true);
            playerTwo.setTurn(false);
        } else {
            playerOne.setTurn(false);
            playerTwo.setTurn(true);
        }
    }

    /**
     * Assign a unique 'Mark' to player one and player two at random.
     *
     * @param playerOne First player
     * @param playerTwo Second player
     */
    private void assignMark(Player playerOne, Player playerTwo) {
        if (random.nextBoolean()) {
            playerOne.setMark(Mark.CIRCLE);
            playerTwo.setMark(Mark.CROSS);
        } else {
            playerOne.setMark(Mark.CROSS);
            playerTwo.setMark(Mark.CIRCLE);
        }
    }

    @Override
    public GameResponse findOne(Long id) {
        notNull(id, "Game id can not be null");

        Game game = gameRepository.findOne(id);
        notNull(game, "Game does not exist");

        return getGameResponse(game);
    }

    @Override
    public GamesSummaryResponse findAll() {
        List<Game> games = (List<Game>) gameRepository.findAll();

        GamesSummaryResponse gamesSummaryResponse = new GamesSummaryResponse();

        for (Game game: games) {
            GameSummary gameSummary = new GameSummary();
            gameSummary.setGameId(game.getId());
            gameSummary.setGameStatus(game.getStatus());
            gameSummary.setUserIds(Arrays.asList(
                    game.getPlayerOne().getUser().getId(),
                    game.getPlayerTwo().getUser().getId()));
            gamesSummaryResponse.addGameSummary(gameSummary);
        }

        return gamesSummaryResponse;
    }

    @Override
    public GameResponse move(MoveRequest move) {
        notNull(move, "Move request can not be null");

        Game game = gameRepository.findOne(move.getGameId());
        notNull(game, "Game does not exist");
        isTrue(game.isInProgress(), "Game needs to be in Progress to make a new move.");

        Player playerOne = game.getPlayerOne();
        Player playerTwo = game.getPlayerTwo();

        notNull(playerOne, "Player one can not be null");
        notNull(playerTwo, "Player two can not be null");

        Player requestingPlayer;
        Player nonRequestingPlayer;

        if (move.getPlayerId().equals(playerOne.getId())) {
            // player one is requester
            requestingPlayer = playerOne;
            nonRequestingPlayer = playerTwo;
        } else if (move.getPlayerId().equals(playerTwo.getId())) {
            // playerTwo is requestor
            requestingPlayer = playerTwo;
            nonRequestingPlayer = playerOne;
        } else {
            throw new IllegalArgumentException("Requesting player id must be part of the Game.");
        }

        isTrue(requestingPlayer.isTurn(), "Must be the requesting players turn in the game.");

        Board board = game.getBoard();
        board.addMove(move.getRow(), move.getColumn(), requestingPlayer.getMark());

        if (board.checkBoardForWin()) {
            requestingPlayer.setStatus(PlayerStatus.WINNER);
            nonRequestingPlayer.setStatus(PlayerStatus.LOOSE);
            game.setStatus(GameStatus.COMPLETE);
        } else if (board.isBoardFull()) {
            requestingPlayer.setStatus(PlayerStatus.TIE);
            nonRequestingPlayer.setStatus(PlayerStatus.TIE);
            game.setStatus(GameStatus.COMPLETE);
        } else {
            requestingPlayer.setTurn(false);
            nonRequestingPlayer.setTurn(true);
        }

        Game save = gameRepository.save(game);

        return getGameResponse(save);
    }

    private GameResponse getGameResponse(Game game) {
        return GameResponse.newBuilder()
                .withGameId(game.getId())
                .withGameStatus(game.getStatus())
                .withPlayers(Arrays.asList(game.getPlayerOne(), game.getPlayerTwo()))
                .withBoard(game.getBoard().to2dArray())
                .build();
    }

}

package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.dl.GameRepository;
import com.mbragg.game.service.api.domain.*;
import com.mbragg.game.service.api.dto.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<Game> saveGameArgumentCaptor;

    @Before
    public void setUp() {
        this.gameService = new GameServiceImpl(gameRepository, userService);
    }

    @Test
    public void testNullCreateGameRequest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Create game request can not be null");

        gameService.create(null);
    }

    @Test
    public void tesCreateGameRequestWithEmptyUsers() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("New Games require two users.");

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(Collections.emptyList());

        gameService.create(createGameRequest);
    }

    @Test
    public void tesCreateGameRequestWithNullUsers() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("New Games require two users.");

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(null);

        gameService.create(createGameRequest);
    }

    @Test
    public void tesCreateGameRequestWithToFewUsers() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("New Games require two users.");

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(Collections.singletonList(1L));

        gameService.create(createGameRequest);
    }

    @Test
    public void testCreateGameWithNonExistentUser() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User two must exist.");

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setBoardSize(3);
        createGameRequest.setUserIds(Arrays.asList(1L, 2L));

        User userOne = new User();
        userOne.setId(1L);
        userOne.setName("user one");

        when(userService.findOne(1L)).thenReturn(userOne);

        gameService.create(createGameRequest);
    }

    @Test
    public void testCreateGameWithInvalidBoardSize() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("The minimum board size is 3.");

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setBoardSize(2);
        createGameRequest.setUserIds(Arrays.asList(1L, 2L));

        User userOne = new User();
        userOne.setId(1L);
        userOne.setName("user one");

        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setName("user two");

        when(userService.findOne(1L)).thenReturn(userOne);
        when(userService.findOne(2L)).thenReturn(userTwo);

        gameService.create(createGameRequest);
    }

    @Test
    public void testCreateGame() {
        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setBoardSize(3);
        createGameRequest.setUserIds(Arrays.asList(1L, 2L));

        User userOne = new User();
        userOne.setId(1L);
        userOne.setName("user one");

        User userTwo = new User();
        userTwo.setId(2L);
        userTwo.setName("user two");

        when(userService.findOne(1L)).thenReturn(userOne);
        when(userService.findOne(2L)).thenReturn(userTwo);

        Game expectedGame = new Game();
        expectedGame.setStatus(GameStatus.IN_PROGRESS);
        expectedGame.setBoard(new Board(3));

        when(gameRepository.save(any(Game.class))).thenReturn(expectedGame);

        GameResponse gameResponse = gameService.create(createGameRequest);

        assertEquals(3, gameResponse.getBoard().length);
        assertEquals(2, gameResponse.getPlayers().size());
        assertEquals(GameStatus.IN_PROGRESS, gameResponse.getGameStatus());
    }

    @Test
    public void testFindOne() {
        Player playerOne = new Player();
        playerOne.setId(1L);
        Player playerTwo = new Player();
        playerTwo.setId(2L);

        Board board = new Board(3);

        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setId(3L);
        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);
        game.setBoard(board);

        when(gameRepository.findOne(3L)).thenReturn(game);
        GameResponse gameResponse = gameService.findOne(3L);

        assertEquals(3L, gameResponse.getGameId().longValue());
        assertEquals(3, gameResponse.getBoard().length);
        assertEquals(2, gameResponse.getPlayers().size());
    }

    @Test
    public void testFindNullGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Game id can not be null");

        gameService.findOne(null);
    }

    @Test
    public void testFindNonExistentGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Game does not exist");

        gameService.findOne(1L);
    }

    @Test
    public void testFindAll() {
        GamesSummaryResponse expectedGamesSummaryResponse = new GamesSummaryResponse();
        GameSummary expectedGameSummary = new GameSummary();
        expectedGameSummary.setGameId(1L);
        expectedGameSummary.setGameStatus(GameStatus.IN_PROGRESS);
        expectedGameSummary.setUserIds(Arrays.asList(1L, 2L));
        expectedGamesSummaryResponse.addGameSummary(expectedGameSummary);

        Game game = new Game();
        game.setId(1L);
        game.setStatus(GameStatus.IN_PROGRESS);

        Player playerOne = new Player();
        Player playerTwo = new Player();
        User userOne = new User(1L, "John");
        User userTwo = new User(2L, "Jim");
        playerOne.setUser(userOne);
        playerTwo.setUser(userTwo);

        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);

        when(gameRepository.findAll()).thenReturn(Collections.singletonList(game));
        GamesSummaryResponse actualGamesSummaryResponse = gameService.findAll();

        assertEquals(1, actualGamesSummaryResponse.getGameSummaries().size());

        GameSummary gameSummary1 = actualGamesSummaryResponse.getGameSummaries().get(0);
        assertEquals(expectedGameSummary.getGameId(), gameSummary1.getGameId());
        assertEquals(expectedGameSummary.getGameStatus(), gameSummary1.getGameStatus());
        assertEquals(2, gameSummary1.getUserIds().size());
    }

    @Test
    public void testNullMove() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Move request can not be null");

        gameService.move(null);
    }

    @Test
    public void testMoveForNonExistentGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Game does not exist");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForNonInProgressGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Game needs to be in Progress to make a new move.");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        Game game = new Game();
        game.setStatus(GameStatus.COMPLETE);
        when(gameRepository.findOne(1L)).thenReturn(game);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForNullProgressGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Game needs to be in Progress to make a new move.");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        Game game = new Game();
        when(gameRepository.findOne(1L)).thenReturn(game);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForNullPlayerOne() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Player one can not be null");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setPlayerOne(null);
        game.setPlayerTwo(new Player());

        when(gameRepository.findOne(1L)).thenReturn(game);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForNullPlayerTwo() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Player two can not be null");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setPlayerOne(new Player());
        game.setPlayerTwo(null);

        when(gameRepository.findOne(1L)).thenReturn(game);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForNoneMemberOfGame() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Requesting player id must be part of the Game.");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);
        moveRequest.setPlayerId(2L);

        Game game = new Game();
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setPlayerOne(new Player());
        game.setPlayerTwo(new Player());

        when(gameRepository.findOne(1L)).thenReturn(game);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForRequestingPlayerWithInvalidTurn() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Must be the requesting players turn in the game.");

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);
        moveRequest.setPlayerId(2L);

        Game expectedGame = new Game();
        expectedGame.setStatus(GameStatus.IN_PROGRESS);

        User userOne = new User("John");
        User userTwo = new User("Jim");

        Player playerOne = new Player(userOne);
        playerOne.setId(1L);
        playerOne.setTurn(true);

        Player playerTwo = new Player(userTwo);
        playerTwo.setId(2L);
        playerTwo.setTurn(false);

        expectedGame.setPlayerOne(playerOne);
        expectedGame.setPlayerTwo(playerTwo);

        when(gameRepository.findOne(1L)).thenReturn(expectedGame);

        gameService.move(moveRequest);
    }

    @Test
    public void testMoveForRequestingPlayerWithValidTurn() {
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);
        moveRequest.setPlayerId(1L);
        moveRequest.setColumn(0);
        moveRequest.setRow(0);

        Game expectedGame = new Game();
        expectedGame.setStatus(GameStatus.IN_PROGRESS);

        User userOne = new User("John");
        User userTwo = new User("Jim");

        Player playerOne = new Player(userOne);
        playerOne.setId(1L);
        playerOne.setTurn(true);
        playerOne.setMark(Mark.CIRCLE);
        Player playerTwo = new Player(userTwo);
        playerTwo.setId(2L);
        playerTwo.setTurn(false);
        playerTwo.setMark(Mark.CROSS);

        expectedGame.setPlayerOne(playerOne);
        expectedGame.setPlayerTwo(playerTwo);

        expectedGame.setBoard(new Board(3));
        expectedGame.setId(1L);

        when(gameRepository.findOne(1L)).thenReturn(expectedGame);

        Game savedGame = new Game();
        savedGame.setBoard(new Board(3));
        when(gameRepository.save(expectedGame)).thenReturn(savedGame);

        gameService.move(moveRequest);

        verify(gameRepository).save(saveGameArgumentCaptor.capture());

        Game caseArgument = saveGameArgumentCaptor.getValue();
        assertEquals(false, caseArgument.getPlayerOne().isTurn());
        assertEquals(true, caseArgument.getPlayerTwo().isTurn());
        assertEquals(GameStatus.IN_PROGRESS, caseArgument.getStatus());
    }

    @Test
    public void testMoveResultingInWin() {
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);
        moveRequest.setPlayerId(1L);
        moveRequest.setColumn(0);
        moveRequest.setRow(0);

        Game expectedGame = new Game();
        expectedGame.setStatus(GameStatus.IN_PROGRESS);

        User userOne = new User("John");
        User userTwo = new User("Jim");

        Player playerOne = new Player(userOne);
        playerOne.setId(1L);
        playerOne.setTurn(true);
        playerOne.setMark(Mark.CIRCLE);

        Player playerTwo = new Player(userTwo);
        playerTwo.setId(2L);
        playerTwo.setTurn(false);
        playerTwo.setMark(Mark.CROSS);

        expectedGame.setPlayerOne(playerOne);
        expectedGame.setPlayerTwo(playerTwo);

        Board board = new Board(3);

        List<BoardRow> rows = board.getRows();

        // Set the board in a winning state for Circle
        for (int i = 0; i < 3; i++) {
            List<Character> boardRow = rows.get(i).getCharacters();
            for (int j = 0; j < 3; j++) {
                if (((i + j) % 2) == 0) {
                    boardRow.set(j, Mark.CIRCLE.getName());
                } else {
                    boardRow.set(j, Mark.CROSS.getName());
                }
            }
        }
        rows.get(0).getCharacters().set(0, Mark.INITIAL.getName());

        expectedGame.setBoard(board);
        expectedGame.setId(1L);

        when(gameRepository.findOne(1L)).thenReturn(expectedGame);

        Game savedGame = new Game();
        savedGame.setBoard(new Board(3));
        when(gameRepository.save(expectedGame)).thenReturn(savedGame);

        gameService.move(moveRequest);

        verify(gameRepository).save(saveGameArgumentCaptor.capture());

        Game caseArgument = saveGameArgumentCaptor.getValue();

        assertEquals(true, caseArgument.getPlayerOne().isTurn());
        assertEquals(PlayerStatus.WINNER, caseArgument.getPlayerOne().getStatus());
        assertEquals(false, caseArgument.getPlayerTwo().isTurn());
        assertEquals(PlayerStatus.LOOSE, caseArgument.getPlayerTwo().getStatus());
        assertEquals(GameStatus.COMPLETE, caseArgument.getStatus());
    }

    @Test
    public void testMoveResultingInTie() {
        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);
        moveRequest.setPlayerId(1L);
        moveRequest.setColumn(0);
        moveRequest.setRow(0);

        Game expectedGame = new Game();
        expectedGame.setStatus(GameStatus.IN_PROGRESS);

        User userOne = new User("John");
        User userTwo = new User("Jim");

        Player playerOne = new Player(userOne);
        playerOne.setId(1L);
        playerOne.setTurn(true);
        playerOne.setMark(Mark.CIRCLE);

        Player playerTwo = new Player(userTwo);
        playerTwo.setId(2L);
        playerTwo.setTurn(false);
        playerTwo.setMark(Mark.CROSS);

        expectedGame.setPlayerOne(playerOne);
        expectedGame.setPlayerTwo(playerTwo);

        Board board = new Board(3);

        List<BoardRow> rows = board.getRows();

        // Set the board in a tie state
        rows.get(0).getCharacters().set(0, Mark.INITIAL.getName());
        rows.get(0).getCharacters().set(1, Mark.CROSS.getName());
        rows.get(0).getCharacters().set(2, Mark.CIRCLE.getName());

        rows.get(1).getCharacters().set(0, Mark.CIRCLE.getName());
        rows.get(1).getCharacters().set(1, Mark.CIRCLE.getName());
        rows.get(1).getCharacters().set(2, Mark.CROSS.getName());

        rows.get(2).getCharacters().set(0, Mark.CROSS.getName());
        rows.get(2).getCharacters().set(1, Mark.CIRCLE.getName());
        rows.get(2).getCharacters().set(2, Mark.CROSS.getName());

        expectedGame.setBoard(board);
        expectedGame.setId(1L);

        when(gameRepository.findOne(1L)).thenReturn(expectedGame);

        Game savedGame = new Game();
        savedGame.setBoard(new Board(3));
        when(gameRepository.save(expectedGame)).thenReturn(savedGame);

        gameService.move(moveRequest);

        verify(gameRepository).save(saveGameArgumentCaptor.capture());

        Game caseArgument = saveGameArgumentCaptor.getValue();

        assertEquals(true, caseArgument.getPlayerOne().isTurn());
        assertEquals(PlayerStatus.TIE, caseArgument.getPlayerOne().getStatus());
        assertEquals(false, caseArgument.getPlayerTwo().isTurn());
        assertEquals(PlayerStatus.TIE, caseArgument.getPlayerTwo().getStatus());
        assertEquals(GameStatus.COMPLETE, caseArgument.getStatus());
    }

}

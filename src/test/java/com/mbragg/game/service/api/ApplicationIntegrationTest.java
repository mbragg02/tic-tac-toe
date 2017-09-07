package com.mbragg.game.service.api;

import com.mbragg.game.service.api.domain.*;
import com.mbragg.game.service.api.dto.CreateGameRequest;
import com.mbragg.game.service.api.dto.GameResponse;
import com.mbragg.game.service.api.dto.MoveRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationIntegrationTest {

    private static final int BOARD_SIZE = 3;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void createUser() {
        ResponseEntity<User> responseEntity =
                restTemplate.postForEntity("/user/", new User("Foo"), User.class);

        User user = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Foo", user.getName());
    }

    @Test
    public void findUser() {
        ResponseEntity<User> responseEntity = restTemplate.postForEntity("/user/", new User("Foo"), User.class);
        Long id = responseEntity.getBody().getId();

        ResponseEntity<User> responseEntity1 =
                restTemplate.getForEntity("/user/" + String.valueOf(id), User.class);

        User user = responseEntity1.getBody();

        assertEquals(HttpStatus.OK, responseEntity1.getStatusCode());
        assertEquals("Foo", user.getName());
    }

    @Test
    public void findAllUsers() {
        restTemplate.postForEntity("/user/", new User("Foo"), User.class);

        List forObject = restTemplate.getForObject("/user/", List.class);

        assertTrue("Should contain more than one User", forObject.size() >= 1);
    }

    @Test
    public void saveGame() {
        User userOne = createPreExistingUser();
        User userTwo = createPreExistingUser();

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(Arrays.asList(userOne.getId(), userTwo.getId()));
        createGameRequest.setBoardSize(BOARD_SIZE);

        ResponseEntity<GameResponse> responseEntity =
                restTemplate.postForEntity("/game/", createGameRequest, GameResponse.class);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        GameResponse gameResponse = responseEntity.getBody();
        assertEquals(1L, gameResponse.getGameId().longValue());
        assertEquals(GameStatus.IN_PROGRESS, gameResponse.getGameStatus());

        Character[][] board = gameResponse.getBoard();
        assertEquals(BOARD_SIZE, board.length);

        assertEmptyBoard(board);
        assertPlayers(userOne, userTwo, gameResponse);
    }

    @Test
    public void findOne() {
        User userOne = createPreExistingUser();
        User userTwo = createPreExistingUser();

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(Arrays.asList(userOne.getId(), userTwo.getId()));
        createGameRequest.setBoardSize(BOARD_SIZE);

        ResponseEntity<GameResponse> createResponseEntity =
                restTemplate.postForEntity("/game/", createGameRequest, GameResponse.class);

        Long gameId = createResponseEntity.getBody().getGameId();

        ResponseEntity<GameResponse> responseEntity =
                restTemplate.getForEntity("/game/" + String.valueOf(gameId), GameResponse.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GameResponse gameResponse = responseEntity.getBody();
        assertEquals(gameId, gameResponse.getGameId());
        assertEquals(GameStatus.IN_PROGRESS, gameResponse.getGameStatus());

        Character[][] board = gameResponse.getBoard();
        assertEquals(BOARD_SIZE, board.length);

        assertEmptyBoard(board);
        assertPlayers(userOne, userTwo, gameResponse);
    }

    @Test
    public void move() {
        User userOne = createPreExistingUser();
        User userTwo = createPreExistingUser();

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setUserIds(Arrays.asList(userOne.getId(), userTwo.getId()));
        createGameRequest.setBoardSize(BOARD_SIZE);

        ResponseEntity<GameResponse> createResponseEntity =
                restTemplate.postForEntity("/game/", createGameRequest, GameResponse.class);

        Long gameId = createResponseEntity.getBody().getGameId();

        MoveRequest moveRequest = new MoveRequest();

        Player activePlayer = createResponseEntity.getBody().getPlayers()
                .stream()
                .filter(Player::isTurn)
                .findFirst().get();

        moveRequest.setGameId(gameId);
        moveRequest.setPlayerId(activePlayer.getId());

        moveRequest.setColumn(0);
        moveRequest.setRow(0);

        ResponseEntity<GameResponse> moveResponseEntity =
                restTemplate.postForEntity("/game/" + String.valueOf(gameId) + "/move", moveRequest, GameResponse.class);

        assertEquals(HttpStatus.OK, moveResponseEntity.getStatusCode());
        GameResponse gameResponse = moveResponseEntity.getBody();
        assertEquals(gameId, gameResponse.getGameId());
        assertEquals(GameStatus.IN_PROGRESS, gameResponse.getGameStatus());

        Character[][] board = gameResponse.getBoard();
        assertEquals(BOARD_SIZE, board.length);

        assertMarkOnBoard(board, 0, 0, activePlayer.getMark());
        assertPlayers(userOne, userTwo, gameResponse);
    }

    private User createPreExistingUser() {
        ResponseEntity<User> UserOneResponseEntity =
                restTemplate.postForEntity("/user/", new User("Foo 1"), User.class);
        return UserOneResponseEntity.getBody();
    }

    private void assertPlayers(User userOne, User userTwo, GameResponse gameResponse) {
        assertEquals(2, gameResponse.getPlayers().size());
        Player playerOne = gameResponse.getPlayers().get(0);
        Player playerTwo = gameResponse.getPlayers().get(1);

        assertEquals(PlayerStatus.PLAYING, playerOne.getStatus());
        assertEquals(PlayerStatus.PLAYING, playerTwo.getStatus());
        assertThat(playerOne.getMark().getName(), isOneOf(Mark.CIRCLE.getName(), Mark.CROSS.getName()));
        assertThat(playerTwo.getMark().getName(), isOneOf(Mark.CIRCLE.getName(), Mark.CROSS.getName()));
        assertThat(playerOne.isTurn(), isOneOf(true, false));
        assertThat(playerTwo.isTurn(), isOneOf(true, false));

        if (playerOne.isTurn()) {
            assertEquals(false, playerTwo.isTurn());
        } else {
            assertEquals(true, playerTwo.isTurn());
        }

        if (playerOne.getMark().equals(Mark.CIRCLE)) {
            assertEquals(Mark.CROSS, playerTwo.getMark());
        } else {
            assertEquals(Mark.CIRCLE, playerTwo.getMark());
        }

        assertEquals(userOne.getId(), playerOne.getUser().getId());
        assertEquals(userTwo.getId(), playerTwo.getUser().getId());
    }

    private void assertEmptyBoard(Character[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                assertEquals('-', board[i][j].charValue());
            }
        }
    }

    private void assertMarkOnBoard(Character[][] board, int row, int col, Mark mark) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i == row && j == col) {
                    assertEquals(mark.getName(), board[i][j]);
                } else {
                    assertEquals('-', board[i][j].charValue());
                }
            }
        }
    }
}

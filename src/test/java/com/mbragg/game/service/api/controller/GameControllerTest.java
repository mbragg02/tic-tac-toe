package com.mbragg.game.service.api.controller;

import com.mbragg.game.service.api.domain.GameStatus;
import com.mbragg.game.service.api.dto.*;
import com.mbragg.game.service.api.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameService gameService;

    @Test
    public void testMove() throws Exception {
        GameResponse gameResponse = GameResponse.newBuilder()
                .withGameId(1L)
                .build();

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setGameId(1L);

        given(this.gameService.move(moveRequest)).willReturn(gameResponse);

        this.mvc.perform(post("/game/1/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"gameId\": 1}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindOne() throws Exception {
        GameResponse gameResponse = GameResponse.newBuilder()
                .withGameId(1L)
                .build();

        given(this.gameService.findOne(1L)).willReturn(gameResponse);

        this.mvc.perform(get("/game/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"gameId\":1}"));
    }

    @Test
    public void testFindAll() throws Exception {
        GamesSummaryResponse gamesSummaryResponse = new GamesSummaryResponse();
        GameSummary gameSummary = new GameSummary();
        gameSummary.setGameId(1L);
        gameSummary.setGameStatus(GameStatus.IN_PROGRESS);
        gameSummary.setUserIds(Arrays.asList(1L, 2L));
        gamesSummaryResponse.addGameSummary(gameSummary);

        given(this.gameService.findAll()).willReturn(gamesSummaryResponse);

        this.mvc.perform(get("/game/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"games\":[{\"gameId\":1,\"userIds\":[1,2],\"gameStatus\":\"IN_PROGRESS\"}]}"));
    }

    @Test
    public void testSaveGame() throws Exception {

        CreateGameRequest createGameRequest = new CreateGameRequest();
        createGameRequest.setBoardSize(1);
        createGameRequest.setUserIds(Arrays.asList(1L, 2L));

        GameResponse gameResponse = GameResponse.newBuilder()
                .withGameId(1L)
                .build();

        given(this.gameService.create(createGameRequest)).willReturn(gameResponse);

        this.mvc.perform(post("/game/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boardSize\": 1, \"userIds\": [1, 2]}"))
                .andExpect(status().isCreated());
    }
}

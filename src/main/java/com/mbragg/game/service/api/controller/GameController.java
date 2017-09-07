package com.mbragg.game.service.api.controller;

import com.mbragg.game.service.api.dto.CreateGameRequest;
import com.mbragg.game.service.api.dto.GameResponse;
import com.mbragg.game.service.api.dto.GamesSummaryResponse;
import com.mbragg.game.service.api.dto.MoveRequest;
import com.mbragg.game.service.api.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.Assert.*;

/**
 * Exposes API for interacting with the Game domain.
 */
@RestController
@RequestMapping(value = "/game", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "game", description = "Operations for creating and playing a Game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation(value = "Create a game")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse save(@RequestBody CreateGameRequest gameRequest) {
        return gameService.create(gameRequest);
    }

    @ApiOperation(value = "Find a game")
    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public GameResponse findOne(@PathVariable("gameId") final Long id) {
        return gameService.findOne(id);
    }

    @ApiOperation(value = "Find all games")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public GamesSummaryResponse findAll() {
        return gameService.findAll();
    }

    @ApiOperation(value = "Perform a move in a game")
    @RequestMapping(value = "/{gameId}/move", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public GameResponse move(@PathVariable("gameId") final Long id, @RequestBody MoveRequest move) {
        isTrue(id.equals(move.getGameId()), "Path and body values for game id should match.");

        return gameService.move(move);
    }

}

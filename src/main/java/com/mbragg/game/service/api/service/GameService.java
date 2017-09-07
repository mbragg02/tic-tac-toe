package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.domain.User;
import com.mbragg.game.service.api.dto.CreateGameRequest;
import com.mbragg.game.service.api.dto.GameResponse;
import com.mbragg.game.service.api.dto.GamesSummaryResponse;
import com.mbragg.game.service.api.dto.MoveRequest;

import java.util.List;

/**
 * Service operations that can be performed on the Game Domain
 */
public interface GameService {

    /**
     * Create a new Game
     *
     * @param gameRequest details of the game to be created
     * @return A GameResponse representing the newly created game
     */
    GameResponse create(CreateGameRequest gameRequest);

    /**
     * Find a specific Game
     *
     * @param id The identifier of a Game
     * @return A GameResponse representing a game
     */
    GameResponse findOne(Long id);

    /**
     * Perform a 'move' in a specific Game
     *
     * @param move Details of the proposed move
     * @return A GameResponse representing a game
     */
    GameResponse move(MoveRequest move);


    /**
     * Find all games
     *
     * @return A List of game responses.
     */
    GamesSummaryResponse findAll();
}

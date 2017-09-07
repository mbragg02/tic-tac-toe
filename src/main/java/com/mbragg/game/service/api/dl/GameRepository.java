package com.mbragg.game.service.api.dl;

import com.mbragg.game.service.api.domain.Game;
import org.springframework.data.repository.CrudRepository;

/**
 * Data repository for retrieving/persisting the Game domain.
 */
public interface GameRepository extends CrudRepository<Game, Long> {
}

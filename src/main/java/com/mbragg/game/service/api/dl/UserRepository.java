package com.mbragg.game.service.api.dl;

import com.mbragg.game.service.api.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Data repository for retrieving/persisting the User domain.
 */
public interface UserRepository extends CrudRepository<User, Long> {
}

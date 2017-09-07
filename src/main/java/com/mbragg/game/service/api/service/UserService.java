package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.domain.User;

import java.util.List;

/**
 * Service operations that can be performed on the Game User
 */
public interface UserService {

    /**
     * Save a user
     *
     * @param user User details to save
     * @return The newly saved user
     */
    User save(User user);

    /**
     * Find a specific user by id
     *
     * @param id The identifier of a user
     * @return The user with the specific id
     */
    User findOne(Long id);

    /**
     * Find all the users
     *
     * @return A list containing all of the users in the system
     */
    List<User> findAll();
}

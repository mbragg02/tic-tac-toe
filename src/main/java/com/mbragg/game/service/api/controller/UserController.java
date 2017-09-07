package com.mbragg.game.service.api.controller;

import com.mbragg.game.service.api.domain.User;
import com.mbragg.game.service.api.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes API for interacting with the User domain.
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "user", description = "Operations for creating and retrieving Users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Create a user")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @ApiOperation(value = "Find a user")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public User findOne(@PathVariable("userId") final Long uuid) {
        return userService.findOne(uuid);
    }

    @ApiOperation(value = "Find all users")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<User> findAll() {
        return userService.findAll();
    }

}

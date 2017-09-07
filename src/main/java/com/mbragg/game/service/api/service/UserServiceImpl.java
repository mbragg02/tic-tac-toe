package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.dl.UserRepository;
import com.mbragg.game.service.api.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.Assert.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        notNull(user, "User must not be null.");
        hasText(user.getName(), "User name must not be null or empty.");

        return userRepository.save(user);
    }

    @Override
    public User findOne(Long id) {
        notNull(id, "User id must not be null.");

        return userRepository.findOne(id);
    }

    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }
}

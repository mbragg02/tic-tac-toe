package com.mbragg.game.service.api.service;

import com.mbragg.game.service.api.dl.UserRepository;
import com.mbragg.game.service.api.domain.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<User> saveUserArgumentCaptor;

    @Before
    public void setUp() {
        this.userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testSaveNullUser() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User must not be null.");

        userService.save(null);
    }

    @Test
    public void testSaveUserWithNullName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User name must not be null or empty.");

        User user = new User();
        userService.save(user);
    }

    @Test
    public void testSaveUserWithEmptyName() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User name must not be null or empty.");

        User user = new User();
        user.setName("");
        userService.save(user);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setName("John");

        User expectedUser = new User();
        expectedUser.setName("John");
        expectedUser.setId(1L);

        when(userRepository.save(user)).thenReturn(expectedUser);

        User savedUser = userService.save(user);

        verify(userRepository).save(saveUserArgumentCaptor.capture());
        User userArgument = saveUserArgumentCaptor.getValue();

        assertEquals("John", userArgument.getName());
        assertEquals("John", savedUser.getName());
        assertEquals(1L, savedUser.getId().longValue());
    }

    @Test
    public void testFindOneNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User id must not be null.");

        userService.findOne(null);
    }

    @Test
    public void testFindOne() {
        User expectedUser = new User();
        expectedUser.setName("John");
        expectedUser.setId(1L);

        when(userRepository.findOne(1L)).thenReturn(expectedUser);
        User foundUser = userService.findOne(1L);

        assertEquals("John", foundUser.getName());
        assertEquals(1L, foundUser.getId().longValue());
    }

    @Test
    public void testFindAll() {
        User expectedUser = new User();
        expectedUser.setName("John");
        expectedUser.setId(1L);

        when(userRepository.findAll()).thenReturn(Collections.singletonList(expectedUser));
        List<User> foundUsers = userService.findAll();

        assertEquals(1, foundUsers.size());
        assertEquals("John", foundUsers.get(0).getName());
        assertEquals(1L, foundUsers.get(0).getId().longValue());
    }
}

package com.mbragg.game.service.api.controller;

import com.mbragg.game.service.api.domain.User;
import com.mbragg.game.service.api.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    public void testFindOne() throws Exception {
        given(this.userService.findOne(1L))
                .willReturn(new User(1L, "name"));

        this.mvc.perform(get("/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"name\"}"));
    }

    @Test
    public void testFindAll() throws Exception {
        given(this.userService.findAll())
                .willReturn(Collections.singletonList(new User(1L, "name")));

        this.mvc.perform(get("/user/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"name\"}]"));
    }

    @Test
    public void testSave() throws Exception {
        given(this.userService.save(new User("name")))
                .willReturn(new User(1L, "name"));

        this.mvc.perform(post("/user/")
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"name\"}"))
                .andExpect(status().isCreated());
    }
}

package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private List<User> users;
    private User newUser;

    @BeforeEach
    void setUp() {

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        users = List.of(
                User.builder().id(1L).email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(2L).email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(3L).email("3user@mail.com").name("3 user test").password("3password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build()
        );

        newUser = User.builder().id(4L).email("newuser@mail.com").name("new user test").password("4password").posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build();

        Mockito.reset(userService, passwordEncoder);

        // userService
        when(userService.create(newUser)).thenReturn(newUser);

         // passwordEncoder
        when(passwordEncoder.encode(anyString())).thenReturn("password");

    }

    @Test
    void testLogin() throws Exception {
        mvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void testRegister() throws Exception {
        mvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void testCreateUser() throws Exception {

        mvc.perform(post("/auth/register")
                        .param("name", newUser.getName())
                        .param("email", newUser.getEmail())
                        .param("password", newUser.getPassword()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/4"));

        verify(userService, times(1)).create(newUser);
        verify(passwordEncoder, times(1)).encode(newUser.getPassword());
    }
}
package com.example.controller;

import com.example.exception.EntityNotFoundException;
import com.example.model.User;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import com.example.utils.security.UsersUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;
    @MockBean
    private SubscriptionService subscriptionService;
    @MockBean
    private UsersUtil usersUtil;

    private List<User> users;
    private User newUser;
    private User currentUser;

    @BeforeEach
    void setUp() {

        // for security
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        users = List.of(
                User.builder().id(1L).email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(2L).email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(3L).email("3user@mail.com").name("3 user test").password("3password").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build()
        );

        newUser = User.builder().id(4L).email("newuser@mail.com").name("new user test").password("4password").posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build();
        currentUser = users.get(0);

        Mockito.reset(userService, subscriptionService, usersUtil);

        // userService
        when(userService.getAll()).thenReturn(users);
        when(userService.getById(-1L)).thenReturn(null);
        when(userService.getByIdEagerly(-1L)).thenReturn(null);
        when(userService.getByIdWithSubscriptions(-1L)).thenReturn(null);
        when(userService.create(newUser)).thenReturn(newUser);
        when(userService.update(-1L, newUser)).thenThrow(EntityNotFoundException.class);
        doNothing().when(userService).deleteById(anyLong());

        for (var user : users) {
            when(userService.getById(user.getId())).thenReturn(user);
            when(userService.getByIdEagerly(user.getId())).thenReturn(user);
            when(userService.getByIdWithSubscriptions(user.getId())).thenReturn(user);
            when(userService.create(user)).thenReturn(newUser);
            when(userService.update(user.getId(), user)).thenReturn(user);
        }

        // subscriptionService
        for (var publisher : users) {
            for (var subscriber : users) {
                doNothing().when(subscriptionService).create(publisher, subscriber);
                doNothing().when(subscriptionService).delete(publisher, subscriber);
            }
        }

        // usersUtil
        when(usersUtil.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void testGetAll() throws Exception {

        String result = mvc.perform(get("/users"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("/users/users"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        for (var user : users) {
            assertTrue(result.contains(user.getName()));
        }

        verify(userService, times(1)).getAll();
    }

    @Test
    void testGetById() throws Exception {

        var user = users.get(1);

        mvc.perform(get("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/users/user"))
                .andExpect(content().string(containsString(user.getName())))
                .andExpect(content().string(containsString("Posts")));

        verify(userService, times(1)).getByIdEagerly(user.getId());
    }

    @Test
    void testCurrentUserPage() throws Exception {

        mvc.perform(get("/users/current-user"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/" + currentUser.getId()));

        verify(usersUtil, atLeast(1)).getCurrentUser();
        verify(usersUtil, atMost(2)).getCurrentUser();
    }

    @Test
    void testSubscribe() throws Exception {

        var user = users.get(1);

        mvc.perform(post("/users/{id}/subscribe", user.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/" + user.getId()));

        verify(userService, times(1)).getByIdWithSubscriptions(user.getId());
        verify(subscriptionService, times(1)).create(user, currentUser);
        verify(usersUtil, atLeast(1)).getCurrentUser();
        verify(usersUtil, atMost(2)).getCurrentUser();
    }

    @Test
    void testUnsubscribe() throws Exception {

        var user = users.get(1);

        mvc.perform(post("/users/{id}/unsubscribe", user.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/" + user.getId()));

        verify(userService, times(1)).getByIdWithSubscriptions(user.getId());
        verify(subscriptionService, never()).delete(user, currentUser);
        verify(usersUtil, atLeast(1)).getCurrentUser();
        verify(usersUtil, atMost(2)).getCurrentUser();
    }
}
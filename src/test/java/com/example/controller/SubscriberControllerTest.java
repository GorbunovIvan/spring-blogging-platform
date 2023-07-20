package com.example.controller;

import com.example.model.Subscription;
import com.example.model.User;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class SubscriberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;
    @MockBean
    private UsersUtil usersUtil;

    private List<User> users;
    private List<Subscription> subscriptions;

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

        subscriptions = List.of(
                Subscription.builder().publisher(users.get(0)).subscriber(users.get(1)).createdAt(LocalDateTime.now()).build(),
                Subscription.builder().publisher(users.get(0)).subscriber(users.get(2)).createdAt(LocalDateTime.now()).build(),
                Subscription.builder().publisher(users.get(1)).subscriber(users.get(0)).createdAt(LocalDateTime.now()).build()
        );

        for (var user : users) {

            user.setSubscriptions(subscriptions.stream()
                    .filter(p -> p.getSubscriber().equals(user))
                    .collect(Collectors.toSet()));

            user.setSubscribers(subscriptions.stream()
                    .filter(p -> p.getPublisher().equals(user))
                    .collect(Collectors.toSet()));
        }

        currentUser = users.get(0);

        Mockito.reset(userService, usersUtil);

        // userService
        when(userService.getByIdWithSubscriptions(-1L)).thenReturn(null);

        for (var user : users) {
            when(userService.getByIdWithSubscriptions(user.getId())).thenReturn(user);
        }

        // usersUtil
        when(usersUtil.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void getAllSubscribersToUser() throws Exception {

        var user = users.get(0);

        String result = mvc.perform(get("/subscriptions/to-user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/subscribers/subscribers"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(result.contains("Subscribers of "));

        for (var subscription : user.getSubscribers()) {
            assertTrue(result.contains(subscription.getSubscriber().getName()));
        }
    }

    @Test
    void getAllSubscriptionsOfUser() throws Exception {

        var user = users.get(0);

        String result = mvc.perform(get("/subscriptions/of-user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/subscribers/subscriptions"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(result.contains("Subscriptions of "));

        for (var subscription : user.getSubscribers()) {
            assertTrue(result.contains(subscription.getPublisher().getName()));
        }
    }
}
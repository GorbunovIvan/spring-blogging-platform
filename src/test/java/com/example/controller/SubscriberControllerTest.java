package com.example.controller;

import com.example.model.Subscription;
import com.example.model.User;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockBean
    private UserService userService;

    private List<User> users;
    private List<Subscription> subscriptions;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).name("1 user test").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(2L).name("2 user test").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build(),
                User.builder().id(3L).name("3 user test").createdAt(LocalDateTime.now()).posts(new HashSet<>()).comments(new HashSet<>()).subscribers(new HashSet<>()).subscriptions(new HashSet<>()).build()
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

        Mockito.reset(userService);

        when(userService.getByIdWithSubscriptions(-1L)).thenReturn(null);

        for (var user : users) {
            when(userService.getByIdWithSubscriptions(user.getId())).thenReturn(user);
        }
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
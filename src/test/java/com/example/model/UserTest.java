package com.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    private List<User> users;
    private List<Subscription> subscriptions;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).name("1 user test").createdAt(LocalDateTime.now()).build(),
                User.builder().id(1L).name("2 user test").createdAt(LocalDateTime.now()).build(),
                User.builder().id(1L).name("3 user test").createdAt(LocalDateTime.now()).build()
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
    }

    @Test
    void testIsSubscribedTo() {

        for (var publisher : users) {
            for (var subscriber : users) {

                boolean isSubscribedTo = subscriber.getSubscriptions().stream()
                        .anyMatch(s -> s.getPublisher().equals(publisher));

                assertEquals(isSubscribedTo, subscriber.isSubscribedTo(publisher));
            }
        }
    }

    @Test
    void testHasSubscriber() {

        for (var publisher : users) {
            for (var subscriber : users) {

                boolean hasSubscriber = publisher.getSubscribers().stream()
                        .anyMatch(s -> s.getSubscriber().equals(subscriber));

                assertEquals(hasSubscriber, publisher.hasSubscriber(subscriber));
            }
        }
    }
}
package com.example.service;

import com.example.model.Subscription;
import com.example.model.User;
import com.example.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SubscriptionServiceTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    private List<User> users;
    private List<Subscription> subscriptions;
    
    private Subscription newSubscription;

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

        newSubscription = Subscription.builder().publisher(users.get(2)).subscriber(users.get(0)).createdAt(LocalDateTime.now()).build();

        Mockito.reset(subscriptionRepository);

        when(subscriptionRepository.save(newSubscription)).thenReturn(newSubscription);
        doNothing().when(subscriptionRepository).delete(any(Subscription.class));

        for (var subscription : subscriptions) {
            when(subscriptionRepository.findByUsersEagerly(subscription.getPublisher(), subscription.getSubscriber())).thenReturn(Optional.of(subscription));
            when(subscriptionRepository.save(subscription)).thenReturn(subscription);
        }

        for (var publisher : users) {
            for (var subscriber : users) {
                if (subscriptions.stream().noneMatch(s -> s.getPublisher().equals(publisher)
                                                        && s.getSubscriber().equals(subscriber))) {
                    when(subscriptionRepository.findByUsersEagerly(publisher, subscriber)).thenReturn(Optional.empty());
                }
            }
        }
    }

    @Test
    void testGetOneEagerly() {

        for (var publisher : users) {
            for (var subscriber : users) {

                var subscription = subscriptions.stream()
                        .filter(s -> s.getPublisher().equals(publisher)
                                     && s.getSubscriber().equals(subscriber))
                        .findAny()
                        .orElse(null);

                assertEquals(subscription, subscriptionService.getOneEagerly(publisher, subscriber));
                verify(subscriptionRepository, times(1)).findByUsersEagerly(publisher, subscriber);
            }
        }
    }

    @Test
    void testCreate() {
        subscriptionService.create(newSubscription);
        assertTrue(newSubscription.getPublisher().getSubscribers().contains(newSubscription));
        assertTrue(newSubscription.getSubscriber().getSubscriptions().contains(newSubscription));
        verify(subscriptionRepository, times(1)).save(newSubscription);
    }

    @Test
    void testDelete() {
        for (var subscription : subscriptions) {
            subscriptionService.delete(subscription);
            assertFalse(newSubscription.getPublisher().getSubscribers().contains(newSubscription));
            assertFalse(newSubscription.getSubscriber().getSubscriptions().contains(newSubscription));
            verify(subscriptionRepository, times(1)).delete(subscription);
        }
    }
}
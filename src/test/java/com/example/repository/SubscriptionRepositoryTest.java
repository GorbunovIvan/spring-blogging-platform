package com.example.repository;

import com.example.model.Subscription;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;

    private List<User> users;
    private List<Subscription> subscriptions;

    @BeforeEach
    void setUp() {

        users = List.of(
                userRepository.save(User.builder().name("1 user test").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().name("2 user test").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().name("3 user test").createdAt(LocalDateTime.now()).build())
        );

        subscriptions = List.of(
                subscriptionRepository.save(Subscription.builder().publisher(users.get(0)).subscriber(users.get(1)).createdAt(LocalDateTime.now()).build()),
                subscriptionRepository.save(Subscription.builder().publisher(users.get(0)).subscriber(users.get(2)).createdAt(LocalDateTime.now()).build()),
                subscriptionRepository.save(Subscription.builder().publisher(users.get(1)).subscriber(users.get(0)).createdAt(LocalDateTime.now()).build())
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
    void testFindByUsersEagerly() {

        for (var publisher : users) {
            for (var subscriber : users) {
                var subscriptionOpt = subscriptionRepository.findByUsersEagerly(publisher, subscriber);
                var subscriptionOptReal = publisher.getSubscribers().stream().filter(s -> s.getSubscriber().equals(subscriber)).findAny();
                assertEquals(subscriptionOptReal, subscriptionOpt);
            }
        }
    }
}
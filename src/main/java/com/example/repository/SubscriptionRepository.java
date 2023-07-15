package com.example.repository;

import com.example.model.Subscription;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("FROM Subscription subscription " +
            "LEFT JOIN FETCH subscription.publisher publisher " +
            "LEFT JOIN FETCH subscription.subscriber subscriber " +
            "LEFT JOIN FETCH publisher.subscriptions subscriptions_of_publisher " +
            "LEFT JOIN FETCH publisher.subscribers subscribers_of_publisher " +
            "LEFT JOIN FETCH subscriber.subscriptions subscriptions_of_subscriber " +
            "LEFT JOIN FETCH subscriber.subscribers subscribers_of_subscriber " +
            "WHERE subscription.publisher = :publisher " +
            "AND subscription.subscriber = :subscriber")
    Optional<Subscription> findByUsersEagerly(User publisher, User subscriber);
}

package com.example.service;

import com.example.model.Subscription;
import com.example.model.User;
import com.example.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription getOneEagerly(User publisher, User subscriber) {
        return subscriptionRepository.findByUsersEagerly(publisher, subscriber)
                .orElse(null);
    }

    public void create(User publisher, User subscriber) {

        var subscription = Subscription.builder()
                .publisher(publisher)
                .subscriber(subscriber)
                .build();

        create(subscription);
    }

    public void create(Subscription subscription) {
        var subscriptionPersisted = subscriptionRepository.save(subscription);
        subscription.setCreatedAt(subscriptionPersisted.getCreatedAt());
        addSubscriptionToUsers(subscription);
    }

    private void addSubscriptionToUsers(Subscription subscription) {
        subscription.getPublisher().getSubscribers().add(subscription);
        subscription.getSubscriber().getSubscriptions().add(subscription);
    }

    @Transactional
    public void delete(User publisher, User subscriber) {
        var subscriptionOpt = subscriptionRepository.findByUsersEagerly(publisher, subscriber);
        if (subscriptionOpt.isPresent()) {
            var subscription = subscriptionOpt.get();
            delete(subscription);
            deleteSubscriptionFromUsers(subscription, publisher, subscriber);
        }
    }

    @Transactional
    public void delete(Subscription subscription) {
        subscriptionRepository.delete(subscription);
        deleteSubscriptionFromUsers(subscription);
    }

    private void deleteSubscriptionFromUsers(Subscription subscription) {
        deleteSubscriptionFromUsers(subscription, subscription.getPublisher(), subscription.getSubscriber());
    }

    private void deleteSubscriptionFromUsers(Subscription subscription, User publisher, User subscriber) {
        publisher.getSubscribers().remove(subscription);
        subscriber.getSubscriptions().remove(subscription);
    }
}

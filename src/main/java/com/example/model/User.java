package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "name", "createdAt" })
@ToString(of = { "id", "name", "createdAt" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 99)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.REFRESH })
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = { CascadeType.REFRESH })
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    private Set<Subscription> subscribers = new HashSet<>();

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions = new HashSet<>();

    @PrePersist
    public void init() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isSubscribedTo(User publisher) {
        return publisher.hasSubscriber(this);
    }

    public boolean hasSubscriber(User subscriber) {

        var hasSubscriber = subscribers.stream()
                .anyMatch(s -> s.getSubscriber().equals(subscriber));

        var isSubscribedTo = subscriber.subscriptions.stream()
                .anyMatch(s -> s.getPublisher().equals(this));

        if (hasSubscriber != isSubscribedTo) {
            throw new RuntimeException(String.format("Some mess with subscription of users with id '%d' and '%d'", subscriber.getId(), getId()));
        }

        return isSubscribedTo;
    }
}

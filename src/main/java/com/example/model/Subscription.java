package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@IdClass(SubscriptionId.class)
@Table(name = "subscriptions")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = { "publisher", "subscriber" })
@ToString
public class Subscription {

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "publisher_id")
    private User publisher;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

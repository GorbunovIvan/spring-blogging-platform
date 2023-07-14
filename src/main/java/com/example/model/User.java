package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate createdAt;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.REFRESH })
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = { CascadeType.REFRESH })
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    private Set<Subscription> subscribersRecords = new HashSet<>();

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private Set<Subscription> subscriptions = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
    }
}

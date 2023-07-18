package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = { "email" })
@ToString(of = { "id", "email", "name", "createdAt" })
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    @NotNull(message = "email is empty")
    @Email(message = "email is not valid")
    private String email;

    @Column(name = "name")
    @NotNull(message = "name is empty")
    @Size(min = 1, max = 99, message = "name should be in range 1 to 99 characters")
    private String name;

    @Column(name = "password")
    @NotNull(message = "password is empty")
    @Size(min = 6, message = "password should have at least 6 characters") // yeah, but bcrypt will encrypt it in long hash string..
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active")
    private boolean isActive;

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

    // Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .toList();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive();
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}

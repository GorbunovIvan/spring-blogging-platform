package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = { "post", "user", "createdAt", "content" })
@ToString(of = { "id", "post", "user", "createdAt", "content" })
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false, length = 256)
    private String content;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

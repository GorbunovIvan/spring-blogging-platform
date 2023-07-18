package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Column(name = "content")
    @NotNull(message = "content is empty")
    @Size(min = 1, max = 256, message = "content should be in range 1 to 256 characters")
    private String content;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "post_id")
    @NotNull(message = "content is empty")
    private Post post;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "user_id")
    @NotNull(message = "content is empty")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void init() {
        this.createdAt = LocalDateTime.now();
    }
}

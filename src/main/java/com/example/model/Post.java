package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = { "title", "content", "createdAt", "user" })
@ToString(of = { "id", "title", "content", "createdAt", "user" })
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 99)
    private String title;

    @Column(name = "content", nullable = false, length = 999)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = { CascadeType.REFRESH })
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Set<LikeOfPost> likes = new HashSet<>();

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    public void addLike(LikeOfPost like) {
        if (like.getPost() == null) {
            like.setPost(this);
        } else if (!like.getPost().getId().equals(getId())) {
            throw new RuntimeException("Wrong post of like during liking");
        }
        likes.add(like);
    }

    public int getNumberOfLikes() {
        return getLikes().size();
    }

    public String getContentShortened() {
        int maxSize = 200;
        if (getContent().length() <= maxSize) {
            return getContent();
        }
        return getContent().substring(0, maxSize-3) + "...";
    }
}

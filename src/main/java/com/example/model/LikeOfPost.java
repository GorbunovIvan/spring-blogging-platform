package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes_of_posts")
@IdClass(value = LikeOfPostId.class)
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode
@ToString
public class LikeOfPost {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "post_id")
    private Post post;
}

package com.example.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class LikeOfPostId implements Serializable {
    private User user;
    private Post post;
}

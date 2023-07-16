package com.example.model;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode @ToString
public class PostDto {
    private String title;
    private String content;
}

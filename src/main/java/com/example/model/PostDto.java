package com.example.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode @ToString
public class PostDto {

    @NotNull(message = "title is empty")
    @Size(min = 1, max = 99, message = "title should be in range 1 to 99 characters")
    private String title;

    @NotNull(message = "content is empty")
    @Size(min = 1, max = 999, message = "content should be in range 1 to 999 characters")
    private String content;

    public Post toPost() {
        return Post.builder()
                .title(getTitle())
                .content(getContent())
                .build();
    }
}

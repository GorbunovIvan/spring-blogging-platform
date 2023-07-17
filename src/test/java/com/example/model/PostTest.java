package com.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    private List<User> users;
    private List<Post> posts;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).name("1 user test").createdAt(LocalDateTime.now()).build(),
                User.builder().id(2L).name("2 user test").createdAt(LocalDateTime.now()).build()
        );

        posts = List.of(
                Post.builder().id(1L).title("1 title post").content("1 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(1)).likes(new HashSet<>()).comments(new HashSet<>()).build(),
                Post.builder().id(2L).title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).likes(new HashSet<>()).comments(new HashSet<>()).build(),
                Post.builder().id(3L).title("3 title post").content("3 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).likes(new HashSet<>()).comments(new HashSet<>()).build()
        );
    }

    @Test
    void testAddLike() {
        for (var post : posts) {
            int numberOflikes = 0;
            for (var user : users) {
                var likeOfPost = LikeOfPost.builder().post(post).user(user).build();
                post.addLike(likeOfPost);
                post.addLike(likeOfPost);
                assertEquals(++numberOflikes, post.getLikes().size());
            }
        }

        var likeOfPost = LikeOfPost.builder().post(posts.get(1)).build();
        assertThrows(RuntimeException.class, () -> posts.get(0).addLike(likeOfPost));
    }

    @Test
    void testGetNumberOfLikes() {
        for (var post : posts) {
            assertEquals(post.getLikes().size(), post.getNumberOfLikes());
        }
    }

    @Test
    void testGetContentShortened() {
        for (var post : posts) {
            var contentShortened = post.getContentShortened();
            if (post.getContent().length() > 200) {
                assertEquals(200, contentShortened.length());
                assertTrue(contentShortened.endsWith("..."));
            } else {
                assertEquals(post.getContent(), contentShortened);
            }
        }
    }
}
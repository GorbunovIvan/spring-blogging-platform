package com.example.repository;

import com.example.model.Comment;
import com.example.model.Post;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private List<Comment> comments;

    private List<Post> posts;

    private List<User> users;

    @BeforeEach
    void setUp() {

        users = List.of(
                userRepository.save(User.builder().email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).build())
        );

        posts = List.of(
                postRepository.save(Post.builder().title("1 title post").content("1 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(1)).build()),
                postRepository.save(Post.builder().title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build())
        );

        comments = List.of(
                commentRepository.save(Comment.builder().content("1 content comment").createdAt(LocalDateTime.now()).post(posts.get(0)).user(users.get(1)).build()),
                commentRepository.save(Comment.builder().content("2 content comment").createdAt(LocalDateTime.now()).post(posts.get(1)).user(users.get(0)).build()),
                commentRepository.save(Comment.builder().content("3 content comment").createdAt(LocalDateTime.now()).post(posts.get(0)).user(users.get(0)).build())
        );

        for (var user : users) {
            user.setPosts(posts.stream()
                    .filter(p -> p.getUser().equals(user))
                    .collect(Collectors.toSet()));
        }

        for (var user : users) {
            user.setComments(comments.stream()
                    .filter(c -> c.getUser().equals(user))
                    .collect(Collectors.toSet()));
        }

        for (var post : posts) {
            post.setComments(comments.stream()
                    .filter(c -> c.getPost().equals(post))
                    .collect(Collectors.toSet()));
        }
    }

    @Test
    void testFindAllByUserIdOrderByCreatedAt() {

        for (var user : users) {
            Set<Comment> comments = new HashSet<>(commentRepository.findAllByUserIdOrderByCreatedAt(user.getId()));
            assertEquals(user.getComments(), comments);
        }

        assertTrue(commentRepository.findAllByUserIdOrderByCreatedAt(-1L).isEmpty());
    }

    @Test
    void testFindAllByPostIdOrderByCreatedAt() {

        for (var post : posts) {
            Set<Comment> comments = new HashSet<>(commentRepository.findAllByPostIdOrderByCreatedAt(post.getId()));
            assertEquals(post.getComments(), comments);
        }

        assertTrue(commentRepository.findAllByPostIdOrderByCreatedAt(-1L).isEmpty());
    }
}
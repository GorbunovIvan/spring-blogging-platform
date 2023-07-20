package com.example.repository;

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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

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
                postRepository.save(Post.builder().title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build()),
                postRepository.save(Post.builder().title("3 title post").content("3 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build())
        );

        for (var user : users) {
            user.setPosts(posts.stream()
                    .filter(p -> p.getUser().equals(user))
                    .collect(Collectors.toSet()));
        }
    }

    @Test
    void testFindByIdEagerly() {

        for (var post : posts) {
            var postOpt = postRepository.findByIdEagerly(post.getId());
            assertTrue(postOpt.isPresent());
            assertEquals(post, postOpt.get());
        }

        assertTrue(postRepository.findByIdEagerly(-1L).isEmpty());
    }

    @Test
    void testFindByIdWithLikes() {

        for (var post : posts) {
            var postOpt = postRepository.findByIdWithLikes(post.getId());
            assertTrue(postOpt.isPresent());
            assertEquals(post, postOpt.get());
        }

        assertTrue(postRepository.findByIdWithLikes(-1L).isEmpty());
    }

    @Test
    void testFindAllByUserId() {

        for (var user : users) {
            Set<Post> posts = new HashSet<>(postRepository.findAllByUserId(user.getId()));
            assertEquals(user.getPosts(), posts);
        }

        assertTrue(postRepository.findAllByUserId(-1L).isEmpty());
    }
}
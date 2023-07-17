package com.example.repository;

import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private List<User> users;

    @BeforeEach
    void setUp() {

        users = List.of(
                userRepository.save(User.builder().name("1 user test").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().name("2 user test").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().name("3 user test").createdAt(LocalDateTime.now()).build())
        );
    }

    @Test
    void testFindByIdEagerly() {

        for (var user : users) {
            var userOpt = userRepository.findByIdEagerly(user.getId());
            assertTrue(userOpt.isPresent());
            assertEquals(user, userOpt.get());
        }

        assertTrue(userRepository.findByIdEagerly(-1L).isEmpty());
    }

    @Test
    void testFindByIdWithSubscriptions() {

        for (var user : users) {
            var userOpt = userRepository.findByIdWithSubscriptions(user.getId());
            assertTrue(userOpt.isPresent());
            assertEquals(user, userOpt.get());
        }

        assertTrue(userRepository.findByIdWithSubscriptions(-1L).isEmpty());
    }
}
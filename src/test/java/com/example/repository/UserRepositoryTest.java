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
                userRepository.save(User.builder().email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).build()),
                userRepository.save(User.builder().email("3user@mail.com").name("3 user test").password("3password").createdAt(LocalDateTime.now()).build())
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
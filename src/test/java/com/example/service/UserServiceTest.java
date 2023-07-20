package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private List<User> users;
    private User newUser;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).build(),
                User.builder().id(2L).email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).build(),
                User.builder().id(3L).email("3user@mail.com").name("3 user test").password("3password").createdAt(LocalDateTime.now()).build()
        );

        newUser = User.builder().id(4L).name("new user test").build();

        Mockito.reset(userRepository);

        when(userRepository.findByEmail("none@mail.com")).thenReturn(Optional.empty());
        when(userRepository.findAllWithRoles()).thenReturn(users);
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());
        when(userRepository.findByIdEagerly(-1L)).thenReturn(Optional.empty());
        when(userRepository.findByIdWithSubscriptions(-1L)).thenReturn(Optional.empty());
        when(userRepository.existsById(-1L)).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);
        doNothing().when(userRepository).deleteById(anyLong());

        for (var user : users) {
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.findByIdEagerly(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.findByIdWithSubscriptions(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.existsById(user.getId())).thenReturn(true);
            when(userRepository.save(user)).thenReturn(user);
        }
    }

    @Test
    void testLoadUserByUsername() {

        for (var user : users) {
            assertEquals(user, userService.loadUserByUsername(user.getEmail()));
            verify(userRepository, times(1)).findByEmail(user.getEmail());
        }

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("none@mail.com"));
        verify(userRepository, times(1)).findByEmail("none@mail.com");
    }

    @Test
    void testGetAll() {
        assertEquals(users, userService.getAll());
        verify(userRepository, times(1)).findAllWithRoles();
    }

    @Test
    void testGetById() {

        for (var user : users) {
            assertEquals(user, userService.getById(user.getId()));
            verify(userRepository, times(1)).findById(user.getId());
        }

        assertNull(userService.getById(-1L));
        verify(userRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetByIdEagerly() {

        for (var user : users) {
            assertEquals(user, userService.getByIdEagerly(user.getId()));
            verify(userRepository, times(1)).findByIdEagerly(user.getId());
        }

        assertNull(userService.getByIdEagerly(-1L));
        verify(userRepository, times(1)).findByIdEagerly(-1L);
    }

    @Test
    void testGetByIdWithSubscriptions() {

        for (var user : users) {
            assertEquals(user, userService.getByIdWithSubscriptions(user.getId()));
            verify(userRepository, times(1)).findByIdWithSubscriptions(user.getId());
        }

        assertNull(userService.getByIdWithSubscriptions(-1L));
        verify(userRepository, times(1)).findByIdWithSubscriptions(-1L);
    }

    @Test
    void testCreate() {
        assertEquals(newUser, userService.create(newUser));
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testUpdate() {

        for (var user : users) {
            user.setName(user.getName() + " updated");
            assertEquals(user, userService.update(user.getId(), user));
            verify(userRepository, times(1)).existsById(user.getId());
            verify(userRepository, times(1)).save(user);
        }

        assertThrows(EntityNotFoundException.class, () -> userService.update(-1L, newUser));
        verify(userRepository, times(1)).existsById(-1L);
        verify(userRepository, never()).save(newUser);
    }

    @Test
    void testDeleteById() {

        for (var user : users) {
            userService.deleteById(user.getId());
            verify(userRepository, times(1)).existsById(user.getId());
            verify(userRepository, times(1)).deleteById(user.getId());
        }

        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(-1L));
        verify(userRepository, times(1)).existsById(-1L);
        verify(userRepository, never()).deleteById(-1L);
    }
}
package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.Post;
import com.example.model.User;
import com.example.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    private List<Post> posts;
    private List<User> users;

    private Post newPost;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).name("1 user test").createdAt(LocalDateTime.now()).build(),
                User.builder().id(2L).name("2 user test").createdAt(LocalDateTime.now()).build()
        );

        posts = List.of(
                Post.builder().id(1L).title("1 title post").content("1 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(1)).build(),
                Post.builder().id(2L).title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build(),
                Post.builder().id(3L).title("3 title post").content("3 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build()
        );

        for (var user : users) {
            user.setPosts(posts.stream()
                    .filter(p -> p.getUser().equals(user))
                    .collect(Collectors.toSet()));
        }

        newPost = Post.builder().id(4L).title("new title post").content("new content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build();

        when(postRepository.findAll()).thenReturn(posts);
        when(postRepository.findAllByUserId(-1L)).thenReturn(Collections.emptyList());
        when(postRepository.findById(-1L)).thenReturn(Optional.empty());
        when(postRepository.findByIdEagerly(-1L)).thenReturn(Optional.empty());
        when(postRepository.findByIdWithLikes(-1L)).thenReturn(Optional.empty());
        when(postRepository.existsById(-1L)).thenReturn(false);
        when(postRepository.save(newPost)).thenReturn(newPost);
        doNothing().when(postRepository).deleteById(anyLong());

        for (var post : posts) {
            when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
            when(postRepository.findByIdEagerly(post.getId())).thenReturn(Optional.of(post));
            when(postRepository.findByIdWithLikes(post.getId())).thenReturn(Optional.of(post));
            when(postRepository.existsById(post.getId())).thenReturn(true);
            when(postRepository.save(post)).thenReturn(post);
        }

        for (var user : users) {
            when(postRepository.findAllByUserId(user.getId())).thenReturn(new ArrayList<>(user.getPosts()));
        }
    }

    @Test
    void testGetAll() {
        assertEquals(posts, postService.getAll());
        verify(postRepository, times(1)).findAll();
    }

    @Test
    void testGetAllByUserId() {

        for (var user : users) {
            assertEquals(new ArrayList<>(user.getPosts()), postService.getAllByUserId(user.getId()));
            verify(postRepository, times(1)).findAllByUserId(user.getId());
        }

        assertTrue(postService.getAllByUserId(-1L).isEmpty());
        verify(postRepository, times(1)).findAllByUserId(-1L);
    }

    @Test
    void testGetById() {

        for (var post : posts) {
            assertEquals(post, postService.getById(post.getId()));
            verify(postRepository, times(1)).findById(post.getId());
        }

        assertNull(postService.getById(-1L));
        verify(postRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetByIdEagerly() {

        for (var post : posts) {
            assertEquals(post, postService.getByIdEagerly(post.getId()));
            verify(postRepository, times(1)).findByIdEagerly(post.getId());
        }

        assertNull(postService.getByIdEagerly(-1L));
        verify(postRepository, times(1)).findByIdEagerly(-1L);
    }

    @Test
    void testGetByIdWithLikes() {

        for (var post : posts) {
            assertEquals(post, postService.getByIdWithLikes(post.getId()));
            verify(postRepository, times(1)).findByIdWithLikes(post.getId());
        }

        assertNull(postService.getByIdWithLikes(-1L));
        verify(postRepository, times(1)).findByIdWithLikes(-1L);
    }

    @Test
    void testCreate() {
        assertEquals(newPost, postService.create(newPost));
        verify(postRepository, times(1)).save(newPost);
    }

    @Test
    void testUpdate() {

        for (var post : posts) {
            post.setTitle(post.getTitle() + " updated");
            assertEquals(post, postService.update(post.getId(), post));
            verify(postRepository, times(1)).existsById(post.getId());
            verify(postRepository, times(1)).save(post);
        }

        assertThrows(EntityNotFoundException.class, () -> postService.update(-1L, newPost));
        verify(postRepository, times(1)).existsById(-1L);
        verify(postRepository, never()).save(newPost);
    }

    @Test
    void testDeleteById() {

        for (var post : posts) {
            postService.deleteById(post.getId());
            verify(postRepository, times(1)).existsById(post.getId());
            verify(postRepository, times(1)).deleteById(post.getId());
        }

        assertThrows(EntityNotFoundException.class, () -> postService.deleteById(-1L));
        verify(postRepository, times(1)).existsById(-1L);
        verify(postRepository, never()).deleteById(-1L);
    }
}
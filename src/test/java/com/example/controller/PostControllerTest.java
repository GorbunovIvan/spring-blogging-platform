package com.example.controller;

import com.example.exception.EntityNotFoundException;
import com.example.model.Post;
import com.example.model.User;
import com.example.service.PostService;
import com.example.utils.security.UsersUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostService postService;
    @MockBean
    private UsersUtil usersUtil;

    private List<Post> posts;
    private List<User> users;

    private Post newPost;
    private User currentUser;

    @BeforeEach
    void setUp() {

        users = List.of(
                User.builder().id(1L).name("1 user test").createdAt(LocalDateTime.now()).build(),
                User.builder().id(2L).name("2 user test").createdAt(LocalDateTime.now()).build()
        );

        posts = List.of(
                Post.builder().id(1L).title("1 title post").content("1 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(1)).likes(new HashSet<>()).build(),
                Post.builder().id(2L).title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).likes(new HashSet<>()).build(),
                Post.builder().id(3L).title("3 title post").content("3 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).likes(new HashSet<>()).build()
        );

        for (var user : users) {
            user.setPosts(posts.stream()
                    .filter(p -> p.getUser().equals(user))
                    .collect(Collectors.toSet()));
        }

        newPost = Post.builder().id(4L).title("new title post").content("new content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build();
        currentUser = users.get(0);

        Mockito.reset(postService, usersUtil);

        // postService
        when(postService.getAll()).thenReturn(posts);
        when(postService.getAllByUserId(-1L)).thenReturn(Collections.emptyList());
        when(postService.getById(-1L)).thenReturn(null);
        when(postService.getByIdEagerly(-1L)).thenReturn(null);
        when(postService.getByIdWithLikes(-1L)).thenReturn(null);
        when(postService.create(newPost)).thenReturn(newPost);
        when(postService.update(-1L, newPost)).thenThrow(EntityNotFoundException.class);
        doNothing().when(postService).deleteById(anyLong());

        for (var post : posts) {
            when(postService.getById(post.getId())).thenReturn(post);
            when(postService.getByIdEagerly(post.getId())).thenReturn(post);
            when(postService.getByIdWithLikes(post.getId())).thenReturn(post);
            when(postService.create(post)).thenReturn(newPost);
            when(postService.update(post.getId(), post)).thenReturn(post);
        }

        for (var user : users) {
            when(postService.getAllByUserId(user.getId())).thenReturn(new ArrayList<>(user.getPosts()));
        }

        // usersUtil
        when(usersUtil.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void testGetAll() throws Exception {

        String result = mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("/posts/posts"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        for (var post : posts) {
            assertTrue(result.contains(post.getTitle()));
            assertTrue(result.contains(post.getContent()));
        }

        verify(postService, times(1)).getAll();
    }

    @Test
    void testGetAllByUserId() throws Exception {

        var user = users.get(0);

        String result = mvc.perform(get("/posts/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/posts/posts"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        for (var post : user.getPosts()) {
            assertTrue(result.contains(post.getTitle()));
            assertTrue(result.contains(post.getContent()));
        }

        verify(postService, times(1)).getAllByUserId(user.getId());
    }

    @Test
    void testGetById() throws Exception {

        var post = posts.get(1);

        mvc.perform(get("/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/posts/post"))
                .andExpect(content().string(containsString(post.getTitle())))
                .andExpect(content().string(containsString(post.getContent())))
                .andExpect(content().string(containsString("Comments")));

        verify(postService, times(1)).getByIdEagerly(post.getId());
    }

    @Test
    void testCreate() throws Exception {

        mvc.perform(post("/posts")
                        .param("title", newPost.getTitle())
                        .param("content", newPost.getContent()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/" + currentUser.getId()));

        verify(postService, times(1)).create(any(Post.class));
        verify(usersUtil, atLeast(1)).getCurrentUser();
        verify(usersUtil, atMost(2)).getCurrentUser();
    }

    @Test
    void testEditForm() throws Exception {

        var post = posts.get(1);

        mvc.perform(get("/posts/{id}/edit", post.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("/posts/edit"))
                .andExpect(content().string(containsString("Editing post " + post.getTitle())))
                .andExpect(content().string(containsString("Update post")));

        verify(postService, times(1)).getById(post.getId());
    }

    @Test
    void testUpdate() throws Exception {

        var post = posts.get(1);

        mvc.perform(patch("/posts/{id}", post.getId())
                        .param("title", "updated title post")
                        .param("content", "updated content post"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts/" + post.getId()));

        verify(postService, times(1)).getById(post.getId());
        verify(postService, times(1)).update(post.getId(), post);
    }

    @Test
    void testLikePost() throws Exception {

        var post = posts.get(1);

        mvc.perform(patch("/posts/{id}/add-like", post.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts/" + post.getId()));

        verify(postService, times(1)).getByIdWithLikes(post.getId());
        verify(postService, times(1)).update(post.getId(), post);
    }

    @Test
    void testDeleteById() throws Exception {

        var post = posts.get(1);

        mvc.perform(delete("/posts/{id}", post.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/" + post.getUser().getId()));

        verify(postService, times(1)).getById(post.getId());
        verify(postService, times(1)).deleteById(post.getId());
    }
}
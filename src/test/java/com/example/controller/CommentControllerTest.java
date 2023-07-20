package com.example.controller;

import com.example.exception.EntityNotFoundException;
import com.example.model.Comment;
import com.example.model.Post;
import com.example.model.User;
import com.example.service.CommentService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CommentService commentService;
    @MockBean
    private PostService postService;
    @MockBean
    private UsersUtil usersUtil;

    private List<Comment> comments;
    private List<Post> posts;
    private List<User> users;

    private Comment newComment;
    private User currentUser;

    @BeforeEach
    void setUp() {

        // for security
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        users = List.of(
                User.builder().id(1L).email("1user@mail.com").name("1 user test").password("1password").createdAt(LocalDateTime.now()).build(),
                User.builder().id(2L).email("2user@mail.com").name("2 user test").password("2password").createdAt(LocalDateTime.now()).build()
        );

        posts = List.of(
                Post.builder().id(1L).title("1 title post").content("1 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(1)).build(),
                Post.builder().id(2L).title("2 title post").content("2 content post").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).user(users.get(0)).build()
        );

        comments = List.of(
                Comment.builder().id(1L).content("1 content comment").createdAt(LocalDateTime.now()).post(posts.get(0)).user(users.get(1)).build(),
                Comment.builder().id(2L).content("2 content comment").createdAt(LocalDateTime.now()).post(posts.get(1)).user(users.get(0)).build(),
                Comment.builder().id(3L).content("3 content comment").createdAt(LocalDateTime.now()).post(posts.get(0)).user(users.get(0)).build()
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

        newComment = Comment.builder().id(4L).content("new content comment").createdAt(LocalDateTime.now()).post(posts.get(0)).user(users.get(0)).build();
        currentUser = users.get(0);

        Mockito.reset(commentService, postService, usersUtil);

        // comments
        when(commentService.getById(-1L)).thenReturn(null);
        when(commentService.create(newComment)).thenReturn(newComment);
        when(commentService.update(-1L, newComment)).thenThrow(EntityNotFoundException.class);
        doNothing().when(commentService).deleteById(anyLong());

        for (var comment : comments) {
            when(commentService.getById(comment.getId())).thenReturn(comment);
            when(commentService.create(comment)).thenReturn(comment);
            when(commentService.update(comment.getId(), comment)).thenReturn(comment);
        }

        // posts
        when(postService.getById(-1L)).thenReturn(null);

        for (var post : posts) {
            when(postService.getById(post.getId())).thenReturn(post);
        }

        // usersUtil
        when(usersUtil.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void testCreate() throws Exception {

        var post = posts.get(1);

        mvc.perform(post("/comments/{postId}", post.getId())
                    .param("content", "new test comment content"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts/" + post.getId()));

        verify(postService, times(1)).getById(post.getId());
        verify(usersUtil, times(1)).getCurrentUser();
        verify(commentService, times(1)).create(any(Comment.class));
    }

    @Test
    void testUpdate() throws Exception {

        var comment = comments.get(1);
        var post = comment.getPost();

        mvc.perform(patch("/comments/{commentId}", comment.getId())
                        .param("content", "updated content"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts/" + post.getId()));

        verify(commentService, times(1)).getById(post.getId());
        verify(commentService, times(1)).update(comment.getId(), comment);
    }

    @Test
    void testDeleteById() throws Exception {

        var comment = comments.get(1);
        var post = comment.getPost();

        mvc.perform(delete("/comments/{commentId}", comment.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/posts/" + post.getId()));

        verify(commentService, times(1)).getById(post.getId());
        verify(commentService, times(1)).deleteById(comment.getId());
    }
}
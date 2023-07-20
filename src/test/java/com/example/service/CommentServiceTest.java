package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.Comment;
import com.example.model.Post;
import com.example.model.User;
import com.example.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    private List<Comment> comments;
    private List<Post> posts;
    private List<User> users;

    private Comment newComment;

    @BeforeEach
    void setUp() {

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

        Mockito.reset(commentRepository);

        when(commentRepository.findAll()).thenReturn(comments);
        when(commentRepository.findAllByUserIdOrderByCreatedAt(-1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByUserIdOrderByCreatedAt(-1L)).thenReturn(Collections.emptyList());
        when(commentRepository.findById(-1L)).thenReturn(Optional.empty());
        when(commentRepository.existsById(-1L)).thenReturn(false);
        when(commentRepository.save(newComment)).thenReturn(newComment);
        doNothing().when(commentRepository).deleteById(anyLong());

        for (var comment : comments) {
            when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
            when(commentRepository.existsById(comment.getId())).thenReturn(true);
            when(commentRepository.save(comment)).thenReturn(comment);
        }

        for (var user : users) {
            when(commentRepository.findAllByUserIdOrderByCreatedAt(user.getId())).thenReturn(new ArrayList<>(user.getComments()));
        }

        for (var post : posts) {
            when(commentRepository.findAllByPostIdOrderByCreatedAt(post.getId())).thenReturn(new ArrayList<>(post.getComments()));
        }
    }

    @Test
    void testGetAll() {
        assertEquals(comments, commentService.getAll());
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void getAllByUserId() {

        for (var user : users) {

            var commentsExpected = new ArrayList<>(user.getComments());
            var comments = commentService.getAllByUserId(user.getId());

            commentsExpected.sort(Comparator.comparing(Comment::getId));
            comments.sort(Comparator.comparing(Comment::getId));

            assertEquals(commentsExpected, comments);
            verify(commentRepository, times(1)).findAllByUserIdOrderByCreatedAt(user.getId());
        }

        assertTrue(commentService.getAllByUserId(-1L).isEmpty());
        verify(commentRepository, times(1)).findAllByUserIdOrderByCreatedAt(-1L);
    }

    @Test
    void getAllByPostId() {

        for (var post : posts) {

            var commentsExpected = new ArrayList<>(post.getComments());
            var comments = commentService.getAllByPostId(post.getId());

            commentsExpected.sort(Comparator.comparing(Comment::getId));
            comments.sort(Comparator.comparing(Comment::getId));

            assertEquals(commentsExpected, comments);
            verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAt(post.getId());
        }

        assertTrue(commentService.getAllByPostId(-1L).isEmpty());
        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAt(-1L);
    }

    @Test
    void testGetById() {

        for (var comment : comments) {
            assertEquals(comment, commentService.getById(comment.getId()));
            verify(commentRepository, times(1)).findById(comment.getId());
        }

        assertNull(commentService.getById(-1L));
        verify(commentRepository, times(1)).findById(-1L);
    }

    @Test
    void testCreate() {
        assertEquals(newComment, commentService.create(newComment));
        verify(commentRepository, times(1)).save(newComment);
    }

    @Test
    void testUpdate() {

        for (var comment : comments) {
            comment.setContent(comment.getContent() + " updated");
            assertEquals(comment, commentService.update(comment.getId(), comment));
            verify(commentRepository, times(1)).existsById(comment.getId());
            verify(commentRepository, times(1)).save(comment);
        }

        assertThrows(EntityNotFoundException.class, () -> commentService.update(-1L, newComment));
        verify(commentRepository, times(1)).existsById(-1L);
        verify(commentRepository, never()).save(newComment);
    }

    @Test
    void testDeleteById() {

        for (var comment : comments) {
            commentService.deleteById(comment.getId());
            verify(commentRepository, times(1)).existsById(comment.getId());
            verify(commentRepository, times(1)).deleteById(comment.getId());
        }

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(-1L));
        verify(commentRepository, times(1)).existsById(-1L);
        verify(commentRepository, never()).deleteById(-1L);
    }
}
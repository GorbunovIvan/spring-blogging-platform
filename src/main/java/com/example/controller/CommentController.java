package com.example.controller;

import com.example.model.Comment;
import com.example.service.CommentService;
import com.example.service.PostService;
import com.example.utils.security.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final UsersUtil usersUtil;

    @PostMapping("/{postId}")
    public String create(@PathVariable Long postId, @RequestParam String content) {

        if (content.isEmpty()) {
            return "redirect:/posts/" + postId;
        }

        var comment = Comment.builder()
                .post(postService.getById(postId))
                .content(content)
                .user(usersUtil.getCurrentUser())
                .build();

        commentService.create(comment);

        return "redirect:/posts/" + postId;
    }

    @PatchMapping("/{commentId}")
    public String update(@PathVariable Long commentId, @RequestParam String content) {

        var comment = commentService.getById(commentId);
        long postId = comment.getPost().getId();

        if (content.isEmpty()) {
            return "redirect:/posts/" + postId;
        }

        comment.setContent(content);
        commentService.update(commentId, comment);

        return "redirect:/posts/" + postId;
    }

    @DeleteMapping("/{commentId}")
    public String deleteById(@PathVariable Long commentId) {
        var comment = commentService.getById(commentId);
        long postId = comment.getPost().getId();
        commentService.deleteById(commentId);
        return "redirect:/posts/" + postId;
    }
}

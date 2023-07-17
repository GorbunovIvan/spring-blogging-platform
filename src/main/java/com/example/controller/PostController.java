package com.example.controller;

import com.example.model.LikeOfPost;
import com.example.model.Post;
import com.example.model.PostDto;
import com.example.model.User;
import com.example.service.PostService;
import com.example.utils.security.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UsersUtil usersUtil;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("posts", postService.getAll());
        return "/posts/posts";
    }

    @GetMapping("/user/{userId}")
    public String getAllByUserId(@PathVariable Long userId, Model model) {
        model.addAttribute("posts", postService.getAllByUserId(userId));
        return "/posts/posts";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable Long id) {
        model.addAttribute("post", postService.getByIdEagerly(id));
        return "/posts/post";
    }

    @PostMapping
    public String create(@ModelAttribute Post post) {
        var currentUser = currentUser();
        if (currentUser == null) {
            throw new RuntimeException("You are not authenticated");
        }
        post.setUser(currentUser);
        postService.create(post);
        return "redirect:/users/" + currentUser.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(Model model, @PathVariable Long id) {
        model.addAttribute("post", postService.getById(id));
        return "/posts/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute PostDto postDto) {

        Post post = postService.getById(id);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        postService.update(id, post);

        return "redirect:/posts/" + id;
    }

    @PatchMapping("/{id}/add-like")
    public String likePost(@PathVariable Long id) {

        Post post = postService.getByIdWithLikes(id);

        var like = LikeOfPost.builder()
                .user(currentUser())
                .post(post)
                .build();

        post.addLike(like);
        postService.update(id, post);

        return "redirect:/posts/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        var post = postService.getById(id);
        postService.deleteById(id);
        return "redirect:/users/" + post.getUser().getId();
    }

    @ModelAttribute("newPost")
    public Post newPost() {
        return Post.builder().build();
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        return usersUtil.getCurrentUser();
    }
}

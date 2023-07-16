package com.example.controller;

import com.example.model.Post;
import com.example.model.User;
import com.example.service.SubscriptionService;
import com.example.service.UserService;
import com.example.utils.security.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final UsersUtil usersUtil;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "/users/users";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable Long id) {
        model.addAttribute("user", userService.getByIdEagerly(id));
        return "/users/user";
    }

    @GetMapping("/current-user")
    public String currentUserPage() {
        var currentUser = usersUtil.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("You are not authenticated");
        }
        return "redirect:/users/" + currentUser.getId();
    }

    @PostMapping("/{id}/subscribe")
    public String subscribe(@PathVariable Long id) {

        var user = userService.getByIdWithSubscriptions(id);
        var currentUser = usersUtil.getCurrentUser();

        if (user.equals(currentUser)) {
            throw new RuntimeException("You can not be subscribed to yourself");
        }

        if (!user.hasSubscriber(currentUser)) {
            subscriptionService.create(user, currentUser);
        }

        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/unsubscribe")
    public String unsubscribe(@PathVariable Long id) {

        var user = userService.getByIdWithSubscriptions(id);
        var currentUser = usersUtil.getCurrentUser();

        if (user.hasSubscriber(currentUser)) {
            subscriptionService.delete(user, currentUser);
        }

        return "redirect:/users/" + id;
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

package com.example.controller;

import com.example.model.Subscription;
import com.example.model.User;
import com.example.service.UserService;
import com.example.utils.security.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriberController {

    private final UserService userService;
    private final UsersUtil usersUtil;

    @GetMapping("/to-user/{userId}")
    public String getAllSubscribersToUser(@PathVariable Long userId, Model model) {
        var user = userService.getByIdWithSubscriptions(userId);
        model.addAttribute("user", user);
        model.addAttribute("users", user.getSubscribers().stream().map(Subscription::getSubscriber).toList());
        return "/subscribers/subscribers";
    }

    @GetMapping("/of-user/{userId}")
    public String getAllSubscriptionsOfUser(@PathVariable Long userId, Model model) {
        var user = userService.getByIdWithSubscriptions(userId);
        model.addAttribute("user", user);
        model.addAttribute("users", user.getSubscriptions().stream().map(Subscription::getPublisher).toList());
        return "/subscribers/subscriptions";
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        return usersUtil.getCurrentUser();
    }
}

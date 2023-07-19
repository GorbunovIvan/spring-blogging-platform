package com.example.controller;

import com.example.model.UserDto;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute @Valid UserDto userDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        var user = userDto.toUser();
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        user = userService.create(user);

        return "redirect:/users/" + user.getId();
    }

    @ModelAttribute("newUser")
    public UserDto newUser() {
        return UserDto.builder().build();
    }
}

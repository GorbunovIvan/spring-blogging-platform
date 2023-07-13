package com.example.utils.security;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
public class UsersUtil {

    private UserService userService;

    private User currentUser;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.currentUser = userService.getAll().stream().findFirst().orElse(null);
    }
}

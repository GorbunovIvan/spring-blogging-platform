package com.example.utils.security;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        String username = principal.toString();
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        }

        return userRepository.findByEmail(username)
                .orElse(null);
    }
}

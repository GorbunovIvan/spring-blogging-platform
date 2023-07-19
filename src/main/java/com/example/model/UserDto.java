package com.example.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;

@Builder
@Getter @Setter
@EqualsAndHashCode(of = { "email" })
@ToString
public class UserDto {

    @NotNull(message = "name is empty")
    @Size(min = 1, max = 99, message = "name should be in range 1 to 99 characters")
    private String name;

    @NotNull(message = "email is empty")
    @Email(message = "email is not valid")
    private String email;

    @NotNull(message = "password is empty")
    @Size(min = 6, message = "password should have at least 6 characters")
    private String password;

    public User toUser() {

        return User.builder()
                .name(getName())
                .email(getEmail())
                .password(getPassword())
                .isActive(true)
                .roles(new HashSet<>())
                .posts(new HashSet<>())
                .comments(new HashSet<>())
                .subscribers(new HashSet<>())
                .subscriptions(new HashSet<>())
                .build();
    }

}

package com.example.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {

    USER(Set.of(Permission.READ_PERMISSION)),
    ADMIN(Set.of(Permission.READ_PERMISSION, Permission.WRITE_PERMISSION));

    private final Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(Permission::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}

package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User user " +
            "LEFT JOIN FETCH user.posts " +
            "LEFT JOIN FETCH user.comments " +
            "LEFT JOIN FETCH user.subscribersRecords " +
            "LEFT JOIN FETCH user.subscriptions " +
            "WHERE user.id = :id")
    Optional<User> findByIdEagerly(Long id);
}
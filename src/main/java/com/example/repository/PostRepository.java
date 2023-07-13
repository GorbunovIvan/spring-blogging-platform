package com.example.repository;

import com.example.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("FROM Post post " +
            "LEFT JOIN FETCH post.comments " +
            "WHERE post.id = :id")
    Optional<Post> findByIdEagerly(Long id);
}

package com.example.repository;

import com.example.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("FROM Post post " +
            "LEFT JOIN FETCH post.comments comments " +
            "LEFT JOIN FETCH post.likes likes " +
            "WHERE post.id = :id ")
    Optional<Post> findByIdEagerly(Long id);

    @Query("FROM Post post " +
            "LEFT JOIN FETCH post.likes likes " +
            "WHERE post.id = :id ")
    Optional<Post> findByIdWithLikes(Long id);

    List<Post> findAllByUserId(Long userId);
}

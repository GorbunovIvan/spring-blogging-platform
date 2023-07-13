package com.example.repository;

import com.example.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Post saveWithDetached(Post post) {
        return entityManager.merge(post);
    }
}

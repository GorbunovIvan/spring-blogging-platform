package com.example.repository;

import com.example.model.Post;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepositoryCustom {

    @Transactional
    Post saveWithDetached(Post post);
}

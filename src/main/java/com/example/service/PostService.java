package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.Post;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public List<Post> getAllByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElse(null);
    }
    
    public Post getByIdEagerly(Long id) {
        return postRepository.findByIdEagerly(id)
                .orElse(null);
    }

    public Post getByIdWithLikes(Long id) {
        return postRepository.getByIdWithLikes(id)
                .orElse(null);
    }

    public Post create(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, Post post) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post", id);
        }
        post.setId(id);
        return postRepository.save(post);
    }

    public void deleteById(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post", id);
        }
        postRepository.deleteById(id);
    }
}

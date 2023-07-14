package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.Comment;
import com.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    public List<Comment> getAllByUserId(Long userId) {
        return commentRepository.findAllByUserId(userId);
    }

    public List<Comment> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElse(null);
    }

    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment update(Long id, Comment comment) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Post", id);
        }
        comment.setId(id);
        return commentRepository.save(comment);
    }

    public void deleteById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment", id);
        }
        commentRepository.deleteById(id);
    }
}

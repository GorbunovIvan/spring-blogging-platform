package com.example.service;

import com.example.exception.EntityNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    public User getByIdEagerly(Long id) {
        return userRepository.findByIdEagerly(id)
                .orElse(null);
    }

    public User getByIdWithSubscriptions(Long id) {
        return userRepository.findByIdWithSubscriptions(id)
                .orElse(null);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User user) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        user.setId(id);
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }
}

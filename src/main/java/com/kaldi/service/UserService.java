package com.kaldi.service;

import com.kaldi.model.User;
import com.kaldi.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    @Transactional
    public User getUser(String username) {
        return userRepository.getByUsername(username);
    }
}

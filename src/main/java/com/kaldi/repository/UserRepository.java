package com.kaldi.repository;

import com.kaldi.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    @Transactional
    public User getByUsername(String username) {
        return find("username", username).firstResult();
    }
}

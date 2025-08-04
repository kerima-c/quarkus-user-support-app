package com.kaldi.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {
    public String hash(String passwordString) {
        return BCrypt.withDefaults().hashToString(12, passwordString.toCharArray());
    }

    public boolean verify(String passwordString, String passwordHash) {
        return BCrypt.verifyer().verify(passwordString.toCharArray(), passwordHash).verified;
    }
}

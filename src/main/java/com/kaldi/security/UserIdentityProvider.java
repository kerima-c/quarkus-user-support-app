package com.kaldi.security;

import com.kaldi.model.User;
import com.kaldi.repository.UserRepository;
import com.kaldi.service.UserService;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class UserIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {
    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordService passwordService;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request, AuthenticationRequestContext context) {
        return context.runBlocking(() -> {
            String username = request.getUsername();
            String password = new String(request.getPassword().getPassword());

            Optional<User> userOptional = Optional.ofNullable(userRepository.getByUsername(username));

            if (userOptional.isEmpty()) {
                throw new AuthenticationFailedException("Invalid credentials");
            }
            User user = userOptional.get();
            String role = user.getUserType().name().toLowerCase();

            if (!passwordService.verify(password, user.getPassword())) {
                throw new AuthenticationFailedException("Invalid credentials");
            }
            return QuarkusSecurityIdentity.builder().setPrincipal(() -> username).addRoles(Set.of(role)).build();
        });
    }
}

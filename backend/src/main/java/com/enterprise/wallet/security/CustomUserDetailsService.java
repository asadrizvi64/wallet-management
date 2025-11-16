package com.enterprise.wallet.security;

import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email or username: {}", emailOrUsername);

        // Try to find user by email first
        Optional<User> userByEmail = userRepository.findByEmail(emailOrUsername);
        if (userByEmail.isPresent()) {
            log.debug("User found by email: {}", emailOrUsername);
            User user = userByEmail.get();
            log.debug("User details - ID: {}, Username: {}, Email: {}, Active: {}",
                user.getId(), user.getUsername(), user.getEmail(), user.getIsActive());
            return UserPrincipal.create(user);
        }

        log.debug("User not found by email, trying username: {}", emailOrUsername);

        // Then try by username
        Optional<User> userByUsername = userRepository.findByUsername(emailOrUsername);
        if (userByUsername.isPresent()) {
            log.debug("User found by username: {}", emailOrUsername);
            User user = userByUsername.get();
            log.debug("User details - ID: {}, Username: {}, Email: {}, Active: {}",
                user.getId(), user.getUsername(), user.getEmail(), user.getIsActive());
            return UserPrincipal.create(user);
        }

        log.error("User not found with email or username: {}", emailOrUsername);
        throw new UsernameNotFoundException("User not found with email or username: " + emailOrUsername);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id: " + id)
                );

        return UserPrincipal.create(user);
    }
}

package com.enterprise.wallet.security;

import com.enterprise.wallet.entity.User;
import com.enterprise.wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Log the search attempt for debugging
        System.out.println("DEBUG: Searching for user with email/username: '" + emailOrUsername + "'");

        // Try to find user by email first
        Optional<User> userByEmail = userRepository.findByEmail(emailOrUsername);
        System.out.println("DEBUG: Search by email result: " + (userByEmail.isPresent() ? "FOUND" : "NOT FOUND"));

        if (userByEmail.isPresent()) {
            System.out.println("DEBUG: Found user with email: " + userByEmail.get().getEmail() + ", username: " + userByEmail.get().getUsername());
            return UserPrincipal.create(userByEmail.get());
        }

        // Try by username
        Optional<User> userByUsername = userRepository.findByUsername(emailOrUsername);
        System.out.println("DEBUG: Search by username result: " + (userByUsername.isPresent() ? "FOUND" : "NOT FOUND"));

        if (userByUsername.isPresent()) {
            System.out.println("DEBUG: Found user with username: " + userByUsername.get().getUsername() + ", email: " + userByUsername.get().getEmail());
            return UserPrincipal.create(userByUsername.get());
        }

        System.out.println("DEBUG: User not found with either email or username: '" + emailOrUsername + "'");
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

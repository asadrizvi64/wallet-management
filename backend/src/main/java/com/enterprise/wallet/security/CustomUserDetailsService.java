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
    @Transactional
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Try to find user by email first, then by username
        User user = userRepository.findByEmail(emailOrUsername)
                .orElse(userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found with email or username: " + emailOrUsername)
                        )
                );

        return UserPrincipal.create(user);
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

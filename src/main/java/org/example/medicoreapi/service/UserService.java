package org.example.medicoreapi.service;

import org.example.medicoreapi.dto.request.RegisterRequest;
import org.example.medicoreapi.entity.User;
import org.example.medicoreapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<UserTokenRevocationService> tokenRevocationService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectProvider<UserTokenRevocationService> tokenRevocationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRevocationService = tokenRevocationService;
    }

    public User createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        log.info("Created user account id={}, username={}, role={}", savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return findUser(id);
    }

    public User updateUser(Long id, RegisterRequest request) {
        User user = findUser(id);

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    public void lockAccount(Long userId) {
        User user = findUser(userId);
        user.setEnabled(false);
        userRepository.save(user);
        revokeAllUserTokens(userId);
        log.info("Locked user account id={}, username={}", user.getId(), user.getUsername());
    }

    public void unlockAccount(Long userId) {
        User user = findUser(userId);
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Unlocked user account id={}, username={}", user.getId(), user.getUsername());
    }

    public void revokeAllUserTokens(Long userId) {
        findUser(userId);
        tokenRevocationService.ifAvailable(service -> service.revokeAllUserTokens(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
}

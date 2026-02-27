package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.InvalidCredentialsException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.User;
import com.revshop.repository.UserRepository;
import com.revshop.service.AuthService;
import com.revshop.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        if (request.getRole().name().equals("SELLER")) {
            user.setBusinessName(request.getBusinessName());
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public User login(LoginRequest request) {

        User user = findUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password for email: " + request.getEmail());
        }

        return user;
    }

    @Override
    public String loginWithToken(LoginRequest request) {
        User user = login(request);
        return JwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = findUserByEmail(request.getEmail());
        user.setResetToken("dummy-reset-token");
        userRepository.save(user);
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        User user = findUserByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password reset successful";
    }

    // ── DRY helper — reusable user lookup ─────────────────────────────
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
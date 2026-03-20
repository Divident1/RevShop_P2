package com.revshop.service.impl;

import com.revshop.dto.*;
import com.revshop.exception.DuplicateResourceException;
import com.revshop.exception.InvalidCredentialsException;
import com.revshop.exception.ResourceNotFoundException;
import com.revshop.model.Role;
import com.revshop.model.User;
import com.revshop.repository.UserRepository;
import com.revshop.util.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Kavya");
        testUser.setEmail("kavya@revshop.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.BUYER);

        registerRequest = new RegisterRequest();
        registerRequest.setName("Kavya");
        registerRequest.setEmail("kavya@revshop.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.BUYER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("kavya@revshop.com");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Register - should register a new buyer successfully")
    void register_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = authService.register(registerRequest);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register - should throw exception when email already exists")
    void register_EmailAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(DuplicateResourceException.class,
                () -> authService.register(registerRequest));

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login - should return user on valid credentials")
    void login_Success() {
        when(userRepository.findByEmail("kavya@revshop.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        User result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("kavya@revshop.com", result.getEmail());
    }

    @Test
    @DisplayName("Login - should throw exception when user not found")
    void login_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.login(loginRequest));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Login - should throw exception on invalid password")
    void login_InvalidPassword() {
        when(userRepository.findByEmail("kavya@revshop.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        Exception exception = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));

        assertTrue(exception.getMessage().contains("Invalid password"));
    }

    @Test
    @DisplayName("ForgotPassword - should set reset token for existing user")
    void forgotPassword_Success() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("kavya@revshop.com");

        when(userRepository.findByEmail("kavya@revshop.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> authService.forgotPassword(request));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("ForgotPassword - should throw exception when user not found")
    void forgotPassword_UserNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@revshop.com");

        when(userRepository.findByEmail("unknown@revshop.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.forgotPassword(request));
    }

    @Test
    @DisplayName("ResetPassword - should reset password successfully")
    void resetPassword_Success() {
        // Generate a real valid reset token and set it on the user
        String resetToken = JwtUtil.generateResetToken("kavya@revshop.com");
        testUser.setResetToken(resetToken);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("kavya@revshop.com");
        request.setNewPassword("newPassword123");
        request.setToken(resetToken);

        when(userRepository.findByEmail("kavya@revshop.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = authService.resetPassword(request);

        assertEquals("Password reset successful", result);
        verify(passwordEncoder, times(1)).encode("newPassword123");
    }

    @Test
    @DisplayName("ResetPassword - should throw exception when user not found")
    void resetPassword_UserNotFound() {
        // Generate a valid token for the unknown email so it passes JWT validation
        String resetToken = JwtUtil.generateResetToken("unknown@revshop.com");

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("unknown@revshop.com");
        request.setNewPassword("newPassword123");
        request.setToken(resetToken);

        when(userRepository.findByEmail("unknown@revshop.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.resetPassword(request));
    }
}

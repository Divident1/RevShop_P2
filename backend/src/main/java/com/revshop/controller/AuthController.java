package com.revshop.controller;

import com.revshop.dto.LoginRequest;
import com.revshop.dto.RegisterRequest;
import com.revshop.dto.ResetPasswordRequest;
import com.revshop.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // allow Angular
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));

    }

    // RESET PASSWORD (THIS IS YOUR FORGOT PASSWORD LOGIC)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

}
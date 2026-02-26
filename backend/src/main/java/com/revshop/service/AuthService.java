package com.revshop.service;

import com.revshop.dto.*;

public interface AuthService {

    String register(RegisterRequest request);

    String login(LoginRequest request);

    String resetPassword(ResetPasswordRequest request);

}
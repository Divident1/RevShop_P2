package com.revshop.service;

import com.revshop.dto.*;
import com.revshop.model.User;

public interface AuthService {

    String register(RegisterRequest request);

    String loginWithToken(LoginRequest request);

    User login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);

}
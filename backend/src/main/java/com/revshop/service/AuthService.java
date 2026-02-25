package com.revshop.service;

import com.revshop.dto.*;

public interface AuthService {

    String register(RegisterRequest request);

    String login(LoginRequest request);

    String forgotPassword(ForgotPasswordRequest request);

//    String resetPassword(ResetPasswordRequest request);


    String resetPasswordDirect(ResetPasswordRequest request);
}
//--> commit each service / controller one by one.
//--> create develop branch  and overlap with remoter branch


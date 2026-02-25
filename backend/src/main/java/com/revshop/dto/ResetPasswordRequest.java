package com.revshop.dto;

public class ResetPasswordRequest {

    private String email;

    private String token;

    private String newPassword;




    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}

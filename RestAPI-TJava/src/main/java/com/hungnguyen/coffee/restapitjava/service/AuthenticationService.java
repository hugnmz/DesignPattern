package com.hungnguyen.coffee.restapitjava.service;

import com.hungnguyen.coffee.restapitjava.dto.request.ResetPasswordDTO;
import com.hungnguyen.coffee.restapitjava.dto.request.SignInRequest;
import com.hungnguyen.coffee.restapitjava.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest signInRequest);


    TokenResponse refresh(HttpServletRequest request);

    String logout(HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(String secretKey);

    String changePassword(ResetPasswordDTO resetPasswordDTO);
}

package com.hungnguyen.coffee.restapitjava.controller;


import com.hungnguyen.coffee.restapitjava.dto.request.ResetPasswordDTO;
import com.hungnguyen.coffee.restapitjava.dto.request.SignInRequest;
import com.hungnguyen.coffee.restapitjava.dto.response.ResponseData;
import com.hungnguyen.coffee.restapitjava.dto.response.TokenResponse;
import com.hungnguyen.coffee.restapitjava.service.AuthenticationService;
import com.hungnguyen.coffee.restapitjava.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor // tạo ra ctor có tham số để inject
@Validated
@Slf4j
@Tag(name = "Authentication Controller")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService  authenticationService;

    @PostMapping("/access")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest request){
        return new ResponseEntity<>(authenticationService.authenticate(request), OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request){
        return new ResponseEntity<>(authenticationService.refresh(request), OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        return new ResponseEntity<>(authenticationService.logout(request), OK);
    }

    //quen email/pass/gui email - co the ko can token


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email){
        return new ResponseEntity<>(authenticationService.forgotPassword(email), OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody String secretKey){
        return new ResponseEntity<>(authenticationService.resetPassword(secretKey), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        return new ResponseEntity<>(authenticationService.changePassword(resetPasswordDTO), HttpStatus.OK);
    }

}

package com.hungnguyen.coffee.restapitjava.service.impl;

import com.hungnguyen.coffee.restapitjava.dto.request.ResetPasswordDTO;
import com.hungnguyen.coffee.restapitjava.dto.request.SignInRequest;
import com.hungnguyen.coffee.restapitjava.dto.response.TokenResponse;
import com.hungnguyen.coffee.restapitjava.model.RedisToken;
import com.hungnguyen.coffee.restapitjava.model.Token;
import com.hungnguyen.coffee.restapitjava.model.User;
import com.hungnguyen.coffee.restapitjava.repository.UserRepository;
import com.hungnguyen.coffee.restapitjava.service.*;
import com.hungnguyen.coffee.restapitjava.utils.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.hungnguyen.coffee.restapitjava.utils.TokenType.RESET_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
     UserService userService;
     UserRepository userRepository;

     AuthenticationManager authenticationManager;

     JWTService jwtService;

     TokenService tokenService;

     PasswordEncoder passwordEncoder;

     RedisTokenService redisTokenService;

    @Override
    public TokenResponse authenticate(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword())); //xac thuc khi truyen vao user nay. ok thi cho truyen buoc tiep theo


        var user =
                userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        //neu thanh cong
        String accessToken = jwtService.generateToken(user);

        String refreshToken = jwtService.generateRefrestToken(user);

        // luu token vao db. de khi logou se xoa di
        //save vao redis

        redisTokenService.save(RedisToken.builder()
                        .id(user.getUSername())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())

//        //tokenService.save(Token.builder()
//                        .username(user.getUsername())
//                        .accessToken(accessToken)
//                        .refreshToken(refreshToken)
//                .build());
        return TokenResponse.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse refresh(HttpServletRequest request) {
        String token = request.getHeader("x-token");
        if(StringUtils.isBlank(token)){
            throw new AuthenticationServiceException("Invalid token");
        }

        // extract user from token
        final String userName = jwtService.extractUsername(token, TokenType.REFRESH_TOKEN);

        //check it into db
        UserDetails user = userRepository.findByUsername(userName);

        if(!jwtService.isValid(token, user)){ // loi
            throw new AuthenticationServiceException("Invalid token");
        }
        String accessToken = jwtService.generateToken(user); // tao accesstoken key mới


        redisTokenService.save(RedisToken.builder()
                .id(user.getUSername()) // loi
                .accessToken(accessToken)
                .refreshToken(accessToken)
                .build())
        return TokenResponse.builder()
                .accessToken(accessToken).refreshToken(token)
                .userId(user.get().getId()).build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION);
        if(StringUtils.isBlank(refreshToken)){
            throw new AuthenticationServiceException("Invalid token");
        }

        final String userName = jwtService.extractUsername(refreshToken, TokenType.ACCESS_TOKEN);
        Token currentToken = tokenService.getByUserName(userName);

         redisTokenService.delete(userName);

        return "Deleted";
    }

    @Override
    public String forgotPassword(String email) {
        //check email exist ko
        User user = userRepository.getUserByEmail(email);

        //user co active hay ko
        if(!user.isEnabled()){
            throw new InvalidDataAccessApiUsageException("Not active user");
        }

        //tao reset token
        String resetToken = jwtService.resetToken(user);

        redisTokenService.save(RedisToken.builder().build());
        //send email confirm link
        String confirmLink = "http://localhost:8080/auth/reset-password";
        return "Sent";
    }

    @Override
    public String resetPassword(String secretKey) {

        final String userName = jwtService.extractUsername(secretKey, RESET_TOKEN);

        //check it into db
        UserDetails user = userRepository.findByUsername(userName);

        if(!jwtService.isValid(secretKey, user, RESET_TOKEN)){
            throw new AuthenticationServiceException("Invalid token");
        }

        redisTokenService.getById(userName);
        return "reseted";
    }

    @Override
    public String changePassword(ResetPasswordDTO resetPasswordDTO) {
        User user = isValidUserByToken(resetPasswordDTO.getSecretKey());

        if(!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())){
            throw new AuthenticationServiceException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));

        userService.saveUser(user);

        return "changed";
    }

    private User isValidUserByToken(String secretKey){
        final String userName = jwtService.extractUsername(secretKey, RESET_TOKEN);
        UserDetails user = userRepository.findByUsername(userName);

        if(user.isEnabled()){
            throw new AuthenticationServiceException("Invalid token");
        }
        if(!jwtService.isValid(secretKey, user, RESET_TOKEN)){
            throw new AuthenticationServiceException("Invalid token");
        }

        return (User) user;
    }
}

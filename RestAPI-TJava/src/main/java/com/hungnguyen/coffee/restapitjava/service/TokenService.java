package com.hungnguyen.coffee.restapitjava.service;

import com.hungnguyen.coffee.restapitjava.exception.ResourceNotFoundException;
import com.hungnguyen.coffee.restapitjava.model.Token;
import com.hungnguyen.coffee.restapitjava.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    public int save(Token token) {
        Optional<Token> optional= tokenRepository.findByUsername(token.getUsername());

        if(optional.isEmpty()) {
            tokenRepository.save(token); // tao moi token
            return token.getId();
        }else{
            Token currentToken = optional.get();
            currentToken.setAccessToken(token.getAccessToken());
            currentToken.setRefreshToken(token.getRefreshToken());
            tokenRepository.findByUsername(token.getUsername());
            return currentToken.getId();
        }

    }

    public String delete(Token token) {
        tokenRepository.delete(token);
        return "deleted";
    }

    public Token getByUserName(String userName){
        return tokenRepository.findByUsername(userName).orElseThrow(() -> new ResourceNotFoundException("Username not found"));
    }
}

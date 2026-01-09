package com.hungnguyen.coffee.restapitjava.service;


import com.hungnguyen.coffee.restapitjava.model.RedisToken;
import com.hungnguyen.coffee.restapitjava.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private RedisTokenRepository redisTokenRepository;

    public String save(RedisToken redisToken) {
        RedisToken result = redisTokenRepository.save(redisToken);

        return result.getId();
    }

    public void delete(String id){
        redisTokenRepository.deleteById(id);
    }

    public RedisToken getById(String id){
        return  redisTokenRepository.findById(id).orElse(null);
    }
}



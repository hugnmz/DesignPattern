package com.hungnguyen.coffee.restapitjava.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("RedisToken")
public class RedisToken implements Serializable{

    private String id;

    private String accessToken;


    private String refreshToken;

    private String resetToken;
}

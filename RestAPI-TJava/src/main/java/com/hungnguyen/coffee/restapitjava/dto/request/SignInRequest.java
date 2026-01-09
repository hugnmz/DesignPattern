package com.hungnguyen.coffee.restapitjava.dto.request;

import com.hungnguyen.coffee.restapitjava.utils.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "username must be not null")
    private String username;

    @NotBlank(message = "password must be not null")
    private String password;

    @NotNull(message = "not null")
    private Platform platform;

    private String deviceToken;
}

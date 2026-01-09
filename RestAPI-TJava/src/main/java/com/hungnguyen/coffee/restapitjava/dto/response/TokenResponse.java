package com.hungnguyen.coffee.restapitjava.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TokenResponse implements Serializable {

    private String accessToken; // key de cho phep vao he thong
    private String refreshToken; // de lam moi token. khi accesstoken het han ma het phien thi gui ngam token nay de
    // refresh lai accessToken moi de ko phai login lai

    private Long userId;

    //moreover
}

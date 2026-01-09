package com.hungnguyen.coffee.restapitjava.dto.response;

import org.springframework.http.HttpStatus;


// khi gặp lỗi thì trả ra lỗi
public class ResponseError extends ResponseData {

    public ResponseError(HttpStatus status, String message) {
        super(status, message);
    }
}

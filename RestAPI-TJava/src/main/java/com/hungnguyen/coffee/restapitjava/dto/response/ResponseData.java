package com.hungnguyen.coffee.restapitjava.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;


//class dùng để mô tả khi dùng description của swagger
// khi truyền dữ liệu vào nó sẽ nhận và mô tả dữ liệu lại cho mình
public class ResponseData<T> {
    private final HttpStatus status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // khi data rong ko hien thi.
    private T data;

    //PUT, patch, delete
    public ResponseData(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    //get post
    public ResponseData(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ResponseData(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

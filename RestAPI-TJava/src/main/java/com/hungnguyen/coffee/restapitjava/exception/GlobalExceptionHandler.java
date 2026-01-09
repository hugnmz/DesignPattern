package com.hungnguyen.coffee.restapitjava.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ghi log
    private static final Logger log = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class}) // annotation nay co the xu li nhieu exception
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // hàm này nhận lỗi từ requst
    public ErrorResponse handleValidationException(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=",""));
        String message = exception.getMessage();
        if(exception instanceof ConstraintViolationException){
            message = message.substring(message.indexOf(" ") + 1);
            errorResponse.setMessage(message);
            errorResponse.setError("Parameter invalid");
        }else if(exception instanceof MethodArgumentNotValidException){
        int start = message.lastIndexOf("[");
        int end = message.lastIndexOf("]");
        message = message.substring(start + 1, end -1);
        errorResponse.setMessage(message);
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        return errorResponse;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Exception exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=",""));
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        if(exception instanceof MethodArgumentTypeMismatchException){
            errorResponse.setMessage("Failed to convert value of type");
        }

        return errorResponse;
    }
}

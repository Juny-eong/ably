package com.ably.assignment.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
@JsonPropertyOrder({"status", "message", "data"})
public class ResponseWrapper<T> {

    /**
     * http response code
     */
    private final int status;

    /**
     * result message
     */
    private final String message;

    /**
     * result data;
     */
    private final T data;

    public ResponseWrapper(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseWrapper<T> wrap(int status, String message, T data) {
        return new ResponseWrapper<>(status, message, data);
    }

    public static <T> ResponseWrapper<T> wrap(String message, T data) {
        return new ResponseWrapper<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> ok(String message, T data) {
        return ResponseEntity.ok(  wrap(message, data)  );
    }



}

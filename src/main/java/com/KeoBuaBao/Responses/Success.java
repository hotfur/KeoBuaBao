package com.KeoBuaBao.Responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Success {
    public static ResponseEntity<Response> NoData(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("OK", message, "")
        );
    }
    public static ResponseEntity<Response> WithData(String message, Object data) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("OK", message, data)
        );
    }
}

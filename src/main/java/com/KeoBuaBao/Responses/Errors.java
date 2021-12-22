package com.KeoBuaBao.Responses;

import com.KeoBuaBao.HelperClass.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Errors {
    public static ResponseEntity<Response> NotFound(String object) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Response("fail",  "The " + object + " was not found on the server", "")
        );
    }

    public static ResponseEntity<Response> NotImplemented(String message) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new Response("fail", message, "")
        );
    }
}

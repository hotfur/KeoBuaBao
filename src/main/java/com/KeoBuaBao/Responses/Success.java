package com.KeoBuaBao.Responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Used to return success API request status to the user
 */
public class Success {
    /**
     * Return a success message only without any entity attached
     * @param message the message to be sent to the user
     * @return a success response entity
     */
    public static ResponseEntity<Response> NoData(String message) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("OK", message, "")
        );
    }

    /**
     * Return a success message and some more data
     * @param message the message to be sent to the user
     * @param data the object to be included in the response
     * @return
     */
    public static ResponseEntity<Response> WithData(String message, Object data) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new Response("OK", message, data)
        );
    }
}

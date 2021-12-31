package com.KeoBuaBao.Responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Generate a response message for the users when there is some errors caught in the request
 * @author Than Doan Thuan
 * @author Vuong Kha Sieu
 * @author Doan Duc Nguyen Long
 * @author Nguyen Van Trang
 */
public class Errors {
    /**
     * Return a message error whenever the object is not found in the database or server
     * @param object the object causes the error
     * @return a message like "The object was not found on the server"
     */
    public static ResponseEntity<Response> NotFound(String object) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Response("fail",  "The " + object + " was not found on the server", "")
        );
    }

    /**
     * Return a message error whenever the server decides not to implement the request from the user without showing
     * the data
     * @param message the message to be notified for the user
     * @return the message
     */
    public static ResponseEntity<Response> NotImplemented(String message) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new Response("fail", message, "")
        );
    }

    /**
     * Return a message error when the request is expired. In this case, the user has to renew the attempt in order to
     * continue access
     * @param object the object causes expiration
     * @return a message like "The object has expired, please renew it to continue"
     */
    public static ResponseEntity<Response> Expired(String object) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new Response("fail", "Your "+ object + " has expired, please renew it to continue.", "")
        );
    }

    /**
     * Return a message error whenever the server decides not to implement the request from the user along with showing
     * the data
     * @param message the message to be notified for the user
     * @param data the data needs to show to the users (everything with an object type in Java is fine)
     * @return status fail along with the message and the data as the respoonse entity
     */
    public static ResponseEntity<Response> NotImplemented(String message, Object data) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new Response("fail", message, data)
        );
    }
}

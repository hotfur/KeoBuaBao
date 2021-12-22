package com.KeoBuaBao.Responses;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Response {
    private String status;
    private String message;
    private Object data;

    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

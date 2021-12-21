package com.KeoBuaBao.HelperClass;

import lombok.Getter;
import lombok.Setter;

public class Response {
    @Getter @Setter private String status;
    @Getter @Setter private String message;
    @Getter @Setter private Object data;

    public Response(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

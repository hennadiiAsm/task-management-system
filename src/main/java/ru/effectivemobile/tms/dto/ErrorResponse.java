package ru.effectivemobile.tms.dto;

import lombok.Data;

@Data
public class ErrorResponse {

    private final Object payload;

    private ErrorResponse(Object payload) {
        this.payload = payload;
    }

    public static ErrorResponse withPayload(Object payload) {
        return new ErrorResponse(payload);
    }

}

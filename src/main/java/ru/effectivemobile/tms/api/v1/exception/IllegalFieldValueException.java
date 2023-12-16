package ru.effectivemobile.tms.api.v1.exception;

import jakarta.validation.ValidationException;

public class IllegalFieldValueException extends ValidationException {

    public IllegalFieldValueException(String constraint, String field, Object value) {
        super(constraint + ". Value {" + value + "} is rejected for field {" + field + "}");
    }

}

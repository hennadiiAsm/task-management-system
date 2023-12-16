package ru.effectivemobile.tms.api.v1.exception;

public class NotAllowedException extends RuntimeException {

    public NotAllowedException(String msg) {
        super(msg);
    }

}

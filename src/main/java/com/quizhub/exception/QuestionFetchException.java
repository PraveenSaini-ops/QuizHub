package com.quizhub.exception;

public class QuestionFetchException extends RuntimeException {
    public QuestionFetchException(String message) {
        super(message);
    }

    public QuestionFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}

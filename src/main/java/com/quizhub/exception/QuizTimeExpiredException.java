package com.quizhub.exception;

public class QuizTimeExpiredException extends RuntimeException {
    public QuizTimeExpiredException(String message) {
        super(message);
    }
}

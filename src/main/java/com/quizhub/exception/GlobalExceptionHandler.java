package com.quizhub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QuizNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleQuizNotFoundException(QuizNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Quiz Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 404);
        return "error/custom-error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 404);
        return "error/custom-error";
    }

    @ExceptionHandler(QuizTimeExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleQuizTimeExpiredException(QuizTimeExpiredException ex, Model model) {
        model.addAttribute("errorTitle", "Quiz Time Expired");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 400);
        return "error/custom-error";
    }

    @ExceptionHandler(InvalidAnswerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidAnswerException(InvalidAnswerException ex, Model model) {
        model.addAttribute("errorTitle", "Invalid Answer Submission");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 400);
        return "error/custom-error";
    }

    @ExceptionHandler(QuestionFetchException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleQuestionFetchException(QuestionFetchException ex, Model model) {
        model.addAttribute("errorTitle", "Question Import Failed");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 503);
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Unexpected Error Occurred");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected server error occurred. Please try again.");
        model.addAttribute("errorCode", 500);
        return "error/custom-error";
    }
}

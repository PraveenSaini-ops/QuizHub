package com.quizhub.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Global Exception Handling Unit Tests")
class ExceptionHandlingTest {

    private GlobalExceptionHandler exceptionHandler;
    private Model model;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        model = new ConcurrentModel();
    }

    @Test
    @DisplayName("Handles QuizNotFoundException returning custom error view")
    void testHandleQuizNotFoundException() {
        QuizNotFoundException ex = new QuizNotFoundException("Quiz with ID 99 not found.");
        String viewName = exceptionHandler.handleQuizNotFoundException(ex, model);

        assertEquals("error/custom-error", viewName);
        assertEquals("Quiz Not Found", model.getAttribute("errorTitle"));
        assertEquals("Quiz with ID 99 not found.", model.getAttribute("errorMessage"));
        assertEquals(404, model.getAttribute("errorCode"));
    }

    @Test
    @DisplayName("Handles QuizTimeExpiredException returning time expired error view")
    void testHandleQuizTimeExpiredException() {
        QuizTimeExpiredException ex = new QuizTimeExpiredException("Quiz duration has expired.");
        String viewName = exceptionHandler.handleQuizTimeExpiredException(ex, model);

        assertEquals("error/custom-error", viewName);
        assertEquals("Quiz Time Expired", model.getAttribute("errorTitle"));
        assertEquals("Quiz duration has expired.", model.getAttribute("errorMessage"));
        assertEquals(400, model.getAttribute("errorCode"));
    }

    @Test
    @DisplayName("Handles InvalidAnswerException returning 400 error view")
    void testHandleInvalidAnswerException() {
        InvalidAnswerException ex = new InvalidAnswerException("At least one option must be marked as correct.");
        String viewName = exceptionHandler.handleInvalidAnswerException(ex, model);

        assertEquals("error/custom-error", viewName);
        assertEquals("Invalid Answer Submission", model.getAttribute("errorTitle"));
        assertEquals("At least one option must be marked as correct.", model.getAttribute("errorMessage"));
        assertEquals(400, model.getAttribute("errorCode"));
    }

    @Test
    @DisplayName("Handles QuestionFetchException returning 503 error view")
    void testHandleQuestionFetchException() {
        QuestionFetchException ex = new QuestionFetchException("Open Trivia DB service unavailable.");
        String viewName = exceptionHandler.handleQuestionFetchException(ex, model);

        assertEquals("error/custom-error", viewName);
        assertEquals("Question Import Failed", model.getAttribute("errorTitle"));
        assertEquals("Open Trivia DB service unavailable.", model.getAttribute("errorMessage"));
        assertEquals(503, model.getAttribute("errorCode"));
    }

    @Test
    @DisplayName("Handles Generic Unexpected Exception returning 500 error view")
    void testHandleGenericException() {
        RuntimeException ex = new RuntimeException("Database connection timeout.");
        String viewName = exceptionHandler.handleGenericException(ex, model);

        assertEquals("error/custom-error", viewName);
        assertEquals("Unexpected Error Occurred", model.getAttribute("errorTitle"));
        assertEquals("Database connection timeout.", model.getAttribute("errorMessage"));
        assertEquals(500, model.getAttribute("errorCode"));
    }
}

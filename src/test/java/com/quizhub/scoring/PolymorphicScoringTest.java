package com.quizhub.scoring;

import com.quizhub.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Polymorphic Question Scoring Logic Unit Tests")
class PolymorphicScoringTest {

    private Topic sampleTopic;

    @BeforeEach
    void setUp() {
        sampleTopic = new Topic("Computer Science", "Core CS concepts", 18);
        sampleTopic.setId(1L);
    }

    @Test
    @DisplayName("McqQuestion: Polymorphic checkAnswer handles single correct, wrong, multiple, and null selections")
    void testMcqQuestionCheckAnswer() {
        McqQuestion mcq = new McqQuestion("What is 2 + 2?", "Basic arithmetic", Difficulty.EASY, sampleTopic);
        mcq.setId(10L);

        Option opt1 = new Option(mcq, "3", false);
        opt1.setId(101L);
        Option opt2 = new Option(mcq, "4", true);
        opt2.setId(102L);
        Option opt3 = new Option(mcq, "5", false);
        opt3.setId(103L);

        mcq.addOption(opt1);
        mcq.addOption(opt2);
        mcq.addOption(opt3);

        // Polymorphic check - correct answer
        assertTrue(mcq.checkAnswer(Collections.singletonList(102L)), "Selecting correct option 102 must return true");

        // Polymorphic check - wrong answer
        assertFalse(mcq.checkAnswer(Collections.singletonList(101L)), "Selecting wrong option 101 must return false");
        assertFalse(mcq.checkAnswer(Collections.singletonList(103L)), "Selecting wrong option 103 must return false");

        // Edge case: Multiple options selected for MCQ
        assertFalse(mcq.checkAnswer(Arrays.asList(101L, 102L)), "Selecting multiple options on single choice MCQ must return false");

        // Edge case: Empty or null
        assertFalse(mcq.checkAnswer(Collections.emptyList()), "Empty selection must return false");
        assertFalse(mcq.checkAnswer(null), "Null selection must return false");

        // Option belonging to a different question
        assertFalse(mcq.checkAnswer(Collections.singletonList(999L)), "Non-existent option must return false");
    }

    @Test
    @DisplayName("TrueFalseQuestion: Polymorphic checkAnswer handles boolean verification")
    void testTrueFalseQuestionCheckAnswer() {
        TrueFalseQuestion tfQuestion = new TrueFalseQuestion("Java is an interpreted-only language.", "Java compiles to bytecode", Difficulty.EASY, sampleTopic);
        tfQuestion.setId(20L);

        Option optTrue = new Option(tfQuestion, "True", false);
        optTrue.setId(201L);
        Option optFalse = new Option(tfQuestion, "False", true);
        optFalse.setId(202L);

        tfQuestion.addOption(optTrue);
        tfQuestion.addOption(optFalse);

        // Correct selection (False)
        assertTrue(tfQuestion.checkAnswer(Collections.singletonList(202L)), "Selecting 'False' (202) must return true");

        // Wrong selection (True)
        assertFalse(tfQuestion.checkAnswer(Collections.singletonList(201L)), "Selecting 'True' (201) must return false");

        // Edge cases
        assertFalse(tfQuestion.checkAnswer(Arrays.asList(201L, 202L)), "Selecting both True and False must return false");
        assertFalse(tfQuestion.checkAnswer(Collections.emptyList()), "Empty selection must return false");
        assertFalse(tfQuestion.checkAnswer(null), "Null selection must return false");
    }

    @Test
    @DisplayName("MultiSelectQuestion: Polymorphic checkAnswer validates exact match of all correct options")
    void testMultiSelectQuestionCheckAnswer() {
        MultiSelectQuestion multi = new MultiSelectQuestion("Which of the following are OOP principles?", "Core OOP Pillars", Difficulty.MEDIUM, sampleTopic);
        multi.setId(30L);

        Option opt1 = new Option(multi, "Encapsulation", true);
        opt1.setId(301L);
        Option opt2 = new Option(multi, "Inheritance", true);
        opt2.setId(302L);
        Option opt3 = new Option(multi, "Polymorphism", true);
        opt3.setId(303L);
        Option opt4 = new Option(multi, "Pagination", false);
        opt4.setId(304L);

        multi.addOption(opt1);
        multi.addOption(opt2);
        multi.addOption(opt3);
        multi.addOption(opt4);

        // Exact match of all correct answers (301, 302, 303) in any order
        assertTrue(multi.checkAnswer(Arrays.asList(301L, 302L, 303L)), "Selecting all correct options must return true");
        assertTrue(multi.checkAnswer(Arrays.asList(303L, 301L, 302L)), "Order of options should not matter");

        // Partial answer (only 2 out of 3 correct options)
        assertFalse(multi.checkAnswer(Arrays.asList(301L, 302L)), "Partial correct answers must return false");

        // Partial correct with an incorrect option included
        assertFalse(multi.checkAnswer(Arrays.asList(301L, 302L, 304L)), "Including incorrect option must return false");

        // All correct plus an incorrect option
        assertFalse(multi.checkAnswer(Arrays.asList(301L, 302L, 303L, 304L)), "All correct plus an incorrect option must return false");

        // Empty / null
        assertFalse(multi.checkAnswer(Collections.emptyList()), "Empty selection must return false");
        assertFalse(multi.checkAnswer(null), "Null selection must return false");
    }

    @Test
    @DisplayName("Polymorphic Collection Iteration: Evaluates heterogeneous question list without if/else branching")
    void testHeterogeneousQuestionListScoring() {
        // Build heterogeneous list of polymorphic Question types
        McqQuestion q1 = new McqQuestion("Q1", "Exp1", Difficulty.EASY, sampleTopic);
        q1.setId(1L);
        Option q1Opt1 = new Option(q1, "A", true);
        q1Opt1.setId(11L);
        q1.addOption(q1Opt1);

        TrueFalseQuestion q2 = new TrueFalseQuestion("Q2", "Exp2", Difficulty.EASY, sampleTopic);
        q2.setId(2L);
        Option q2Opt1 = new Option(q2, "True", true);
        q2Opt1.setId(21L);
        q2.addOption(q2Opt1);

        MultiSelectQuestion q3 = new MultiSelectQuestion("Q3", "Exp3", Difficulty.MEDIUM, sampleTopic);
        q3.setId(3L);
        Option q3Opt1 = new Option(q3, "Opt1", true);
        q3Opt1.setId(31L);
        Option q3Opt2 = new Option(q3, "Opt2", true);
        q3Opt2.setId(32L);
        q3.addOption(q3Opt1);
        q3.addOption(q3Opt2);

        List<Question> questions = Arrays.asList(q1, q2, q3);

        // Simulate student answer map
        java.util.Map<Long, List<Long>> studentAnswers = java.util.Map.of(
                1L, List.of(11L),             // Correct
                2L, List.of(99L),             // Wrong
                3L, List.of(31L, 32L)         // Correct
        );

        int totalCorrect = 0;
        for (Question q : questions) {
            // Polymorphic invocation without type casting or if/else checks
            if (q.checkAnswer(studentAnswers.get(q.getId()))) {
                totalCorrect++;
            }
        }

        assertEquals(2, totalCorrect, "Polymorphic check should evaluate 2 out of 3 questions as correct");
    }
}

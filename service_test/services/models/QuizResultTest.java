package ulb.infof307.g04.services.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import ulb.infof307.g04.enums.CardMasteryLevels;

class QuizResultTest {
    private QuizResult quizResult;

    @BeforeEach
    public void setUp() {
        quizResult = new QuizResult(1, 1);
    }

    @Test
    void testGetId() {
        Assertions.assertEquals(1, quizResult.getId());
    }

    @Test
    void testGetPerformanceType() {
        Assertions.assertEquals(CardMasteryLevels.MODERATE, quizResult.getPerformanceType());
    }

    @Test
    void testConstructorWithJson() throws JsonProcessingException {
        String json = "{\"id\": 2, \"performance_type\": 2}";
        QuizResult quizResultFromJson = new QuizResult(json);
        Assertions.assertEquals(2, quizResultFromJson.getId());
        Assertions.assertEquals(CardMasteryLevels.GOOD, quizResultFromJson.getPerformanceType());
    }

    @Test
    void testToString() {
        String expectedString = "QuizResult{id=1', performance_type='1'}";
        Assertions.assertEquals(expectedString, quizResult.toString());
    }
}

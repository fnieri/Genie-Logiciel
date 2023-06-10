package ulb.infof307.g04.services.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class TagTest {
    private Tag tag;

    @BeforeEach
    public void setUp() {
        tag = new Tag(1, "test");
    }

    @Test
    void testGetId() {
        Assertions.assertEquals(1, tag.getId());
    }

    @Test
    void testGetName() {
        Assertions.assertEquals("test", tag.getName());
    }

    @Test
    void testConstructorWithJson() throws JsonProcessingException {
        String json = "{\"id\": 2, \"name\": \"json test\"}";
        Tag tagFromJson = new Tag(json);
        Assertions.assertEquals(2, tagFromJson.getId());
        Assertions.assertEquals("json test", tagFromJson.getName());
    }

    @Test
    void testToString() {
        String expectedString = "Tag{id=1', name='test'}";
        Assertions.assertEquals(expectedString, tag.toString());
    }
}

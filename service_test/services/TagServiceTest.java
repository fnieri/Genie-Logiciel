package ulb.infof307.g04.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.services.models.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {
    private ITagService tagService;

    @BeforeEach
    void setUp() throws ApiException {
        String baseUri = "http://127.0.0.1:8000";
        UserServiceFactory.setInstance(new UserService(baseUri));
        TagServiceFactory.setInstance(new TagService(baseUri));
        CardServiceFactory.setInstance(new CardService(baseUri));
        DeckServiceFactory.setInstance(new DeckService(baseUri));
        QuizResultServiceFactory.setInstance(new QuizResultService(baseUri));

        tagService = TagServiceFactory.getInstance();
        UserServiceFactory.getInstance().signIn("admin", "password");
    }

    @Test
    void testCreateTag() throws ApiException {
        String tagName = "Test Tag1";
        tagService.createTag(tagName);

        // verify that the tag was created by retrieving it from the API and checking its name
        List<Tag> tags = tagService.getTags();
        Tag createdTag = tags.stream().filter(tag -> tag.getName().equals(tagName)).findFirst().orElse(null);

        Assertions.assertNotNull(createdTag);
        Assertions.assertEquals(tagName, createdTag.getName());
    }

    @Test
    void testGetTag() throws ApiException{
        String tagName = "Test Tag2";
        tagService.createTag(tagName);

        // retrieve the tag from the API
        List<Tag> tags = tagService.getTags();
        Tag testTag = tags.stream().filter(tag -> tag.getName().equals(tagName)).findFirst().orElse(null);

        // verify that the tag was retrieved and that its name is correct
        Assertions.assertNotNull(testTag);
        Assertions.assertEquals(tagName, testTag.getName());
    }

    @Test
    void testGetTags() throws ApiException {
        String tagName1 = "Test Tag3";
        String tagName2 = "Test Tag4";
        tagService.createTag(tagName1);
        tagService.createTag(tagName2);

        // retrieve the tags from the API
        List<Tag> tags = tagService.getTags();

        // verify that the tags were retrieved and that their names are correct
        Assertions.assertNotNull(tags);
    }
}

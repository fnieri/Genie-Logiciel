
package ulb.infof307.g04.services;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.NotFoundException;
import ulb.infof307.g04.exceptions.deck.*;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.services.models.Deck;

import java.util.ArrayList;

class DeckServiceTest {
    private IDeckService deckService;

    @BeforeEach
    void setUp() throws ApiException {
        String baseUri = "http://127.0.0.1:8000";
        UserServiceFactory.setInstance(new UserService(baseUri));
        TagServiceFactory.setInstance(new TagService(baseUri));
        CardServiceFactory.setInstance(new CardService(baseUri));
        DeckServiceFactory.setInstance(new DeckService(baseUri));
        QuizResultServiceFactory.setInstance(new QuizResultService(baseUri));

        deckService = DeckServiceFactory.getInstance();
        UserServiceFactory.getInstance().signIn("ben", "ben1234ben");
    }

    @Test
    void testCreateDeck() throws ApiException {
        int deckId = deckService.createDeck("Test Deck", new ArrayList<>());
        Deck deck = deckService.getDeck(deckId);
        Assert.assertEquals("Test Deck", deck.getName());
        Assert.assertEquals("ben", deck.getUsers().get(0));
        deckService.deleteDeck(deckId);
    }

    @Test
    void testGetDeck() throws ApiException {
        int deckId = deckService.createDeck("Test Deck1", new ArrayList<>());
        Deck deck = deckService.getDeck(deckId);
        Assert.assertEquals(deckId, deck.getId());
        deckService.deleteDeck(deckId);
    }

    @Test
    void testUpdateDeckName() throws DeckCreationError, DeckUpdateError, DeckDoesNotExist, ApiException {
        int deckId = deckService.createDeck("Test Deck2", new ArrayList<>());
        String newName = "New Name";
        deckService.updateDeckName(deckId, newName);
        Assert.assertEquals(newName, deckService.getDeck(deckId).getName());
        deckService.deleteDeck(deckId);
    }

    @Test
    void testDeleteDeck() throws ApiException {
        int deckId = deckService.createDeck("Test Deck4", new ArrayList<>());
        deckService.deleteDeck(deckId);
        Assert.assertThrows(NotFoundException.class, () -> deckService.getDeck(deckId));
    }

    @Test
    void testGetUserDeckStatistics() throws ApiException {
        int deckId = deckService.createDeck("Test Deck5", new ArrayList<>());
        Assert.assertNotNull(deckService.getUserDeckStatistics(deckId));
        deckService.deleteDeck(deckId);
    }
}

package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.ApiException;

class TestCardServiceCreateCard {
    private String baseUrl = "http://localhost:8000";

    @BeforeAll
    static void setUp() {
        CacheService.getInstance().setEnabled(false);
    }

    @Test
    void testCreateCard() throws IOException, InterruptedException, ApiException {
        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock();

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"id\": 1, \"question\": \"Question 1\", \"answer\": \"Answer 1\" }");
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        CardService service = new CardService(baseUrl, client);
        // Simulate the call to sendCreateObjectRequest to return the mocked response
        int cardId = service.createCard(
            0,
            "question 1",
            CardType.fromViewString("PLAIN_TEXT"),
            "answer 1",
            CardType.fromViewString("PLAIN_TEXT")
        );
        assertEquals(1, cardId);
    }

}

//    @Test
//    void testUpdateCard() throws IOException, InterruptedException, ApiException {
//        HttpClient client = mock(HttpClient.class);
//        HttpResponse<String> response = mock();
//
//        when(response.statusCode()).thenReturn(200);
//
//        CardService service = new CardService(baseUrl, client);
//        // Simulate the call to sendUpdateObjectRequest to return the mocked response
//        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
//
//        Card card = new Card();
//        card.setId(1);
//        card.setQuestion("Updated Question");
//        card.setAnswer("Updated Answer");
//
//        service.updateCard(card);
//
//        // Verify that sendUpdateObjectRequest was called with the expected parameters
//        verify(client).send(any(), any());
//    }
//
//    @Test
//    void testDeleteCard() throws IOException, InterruptedException, ApiException {
//        HttpClient client = mock(HttpClient.class);
//        HttpResponse<String> response = mock();
//
//        when(response.statusCode()).thenReturn(204);
//
//        CardService service = new CardService(baseUrl, client);
//        // Simulate the call to sendDeleteObjectRequest to return the mocked response
//        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
//
//        int cardId = 1;
//
//        service.deleteCard(cardId);
//
//        // Verify that sendDeleteObjectRequest was called with the expected parameters
//        verify(client).send(any(), any());
//    }
//
//    @Test
//    void testGetCardById() throws IOException, InterruptedException, ApiException {
//        HttpClient client = mock(HttpClient.class);
//        HttpResponse<String> response = mock();
//
//        when(response.statusCode()).thenReturn(200);
//        when(response.body()).thenReturn("{\"id\": 1, \"question\": \"Question 1\", \"answer\": \"Answer 1\"}");
//
//        CardService service = new CardService(baseUrl, client);
//        // Simulate the call to sendFetchObjectRequest to return the mocked response
//        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
//
//        int cardId = 1;
//
//        Card card = service.getCardById(cardId);
//
//        // Verify that sendFetchObjectRequest was called with the expected parameters
//        verify(client).send(any(), any());
//        assertEquals(1, card.getId());
//        assertEquals("Question 1", card.getQuestion());
//        assertEquals("Answer 1", card.getAnswer());
//    }
//
//    @Test
//    void testGetCardsByTag() throws IOException, InterruptedException, ApiException {
//        HttpClient client = mock(HttpClient.class);
//        HttpResponse<String> response = mock();
//
//        when(response.statusCode()).thenReturn(200);
//        when(response.body()).thenReturn("[{\"id\": 1, \"question\": \"Question 1\", \"answer\": \"Answer 1\"}, {\"id\": 2, \"question\": \"Question 2\", \"answer\": \"Answer 2\"}]");
//
//        CardService service = new CardService(baseUrl, client);
//        // Simulate the call to sendFetchAllObjectRequest to return the mocked response
//        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
//
//        String tagName = "Tag 1";
//
//        List<Card> cards = service.getCardsByTag(tagName);
//
//        // Verify that sendFetchAllObjectRequest was called with the expected parameters
//        verify(client).send(any(), any());
//        assertEquals(2, cards.size());
//
//        Card firstCard = cards.get(0);
//        assertEquals(1, firstCard.getId());
//        assertEquals("Question 1", firstCard.getQuestion());
//        assertEquals("Answer 1", firstCard.getAnswer());
//
//        Card secondCard = cards.get(1);
//        assertEquals(2, secondCard.getId());
//        assertEquals("Question 2", secondCard.getQuestion());
//        assertEquals("Answer 2", secondCard.getAnswer());
//    }
//
//    @Test
//    void testGetAllCards() throws IOException, InterruptedException, ApiException {
//        HttpClient client = mock(HttpClient.class);
//        HttpResponse<String> response = mock();
//
//        when(response.statusCode()).thenReturn(200);
//        when(response.body()).thenReturn("[{\"id\": 1, \"question\": \"Question 1\", \"answer\": \"Answer 1\"}, {\"id\": 2, \"question\": \"Question 2\", \"answer\": \"Answer 2\"}]");
//
//        CardService service = new CardService(baseUrl, client);
//        // Simulate the call to sendFetchAllObjectRequest to return the mocked response
//        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
//
//        List<Card> cards = service.getAllCards();
//
//        // Verify that sendFetchAllObjectRequest was called with the expected parameters
//        verify(client).send(any(), any());
//        assertEquals(2, cards.size());
//
//        Card firstCard = cards.get(0);
//        assertEquals(1, firstCard.getId());
//        assertEquals("Question 1", firstCard.getQuestion());
//        assertEquals("Answer 1", firstCard.getAnswer());
//
//        Card secondCard = cards.get(1);
//        assertEquals(2, secondCard.getId());
//        assertEquals("Question 2", secondCard.getQuestion());
//        assertEquals("Answer 2", secondCard.getAnswer());
//    }
//}}

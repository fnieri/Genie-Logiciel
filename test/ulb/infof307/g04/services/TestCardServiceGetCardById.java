package ulb.infof307.g04.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.api.*;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.interfaces.services.ICardService;
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.models.Tag;
import ulb.infof307.g04.patterns.CardFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TestCardServiceGetCardById {

    private String getCardsJsonResponse;

    @BeforeAll
    static void setUpAll() {
        // FIXME: this sould be done by injecting the cache service into
        // the other services and mocking it.
        CacheService.getInstance().setEnabled(false);
    }

    @BeforeEach
    void setUpEach() throws IOException, URISyntaxException {
        URL file = getClass().getResource("/ulb/infof307/g04/services/getCards.json");
        getCardsJsonResponse = Files.readString(Path.of(file.toURI()));
    }

    @Test
    public void testGetCardById() throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ICardService service = spy(new CardService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(getCardsJsonResponse);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        CardFactory cardFactory= new CardFactory();
        ICard card = cardFactory.makeCard(
                1,
                1,
                "Question",
                "answer",
                CardType.PLAIN_TEXT,
                CardType.PLAIN_TEXT,
                CardMasteryLevels.NOT_YET_LEARNED
        );
        assertEquals(1, card.getId());
    }
    @ParameterizedTest
    @MethodSource("getTestGetCardByIdErrorArguments")
    public <T extends ApiException> void testGetCardById_Error_ReturnsException(int statusCode, Class<T> expectedError) throws IOException, ApiException, InterruptedException {
        HttpResponse<String> response = mock(HttpResponse.class);
        HttpClient client = mock();
        ICardService service = spy(new CardService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn("Error Message");
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        assertThrows(expectedError, () -> {
            service.getCard(1);
        });
    }
    private static Stream<Arguments> getTestGetCardByIdErrorArguments() {
        return Stream.of(
                Arguments.of(404, NotFoundException.class),
                Arguments.of(403, ForbiddenException.class),
                Arguments.of(500, ServerException.class)
        );
    }

}


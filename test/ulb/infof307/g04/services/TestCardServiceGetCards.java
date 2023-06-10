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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TestCardServiceGetCards {
    private String getCardsJsonRequest;
    @BeforeAll
    static void setUpAll() {
        CacheService.getInstance().setEnabled(false);
    }

    @BeforeEach
    void setUpEach() throws IOException, URISyntaxException {
        URL file = getClass().getResource("/ulb/infof307/g04/services/getCards.json");
        getCardsJsonRequest = Files.readString(Path.of(file.toURI()));
    }

    @Test
    public void testGetCards() throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ICardService service = spy(new CardService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(getCardsJsonRequest);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);
        List<ICard> cards = service.getCards();
        assertEquals(2, cards.size());

    }

    @ParameterizedTest
    @MethodSource("getTestGetCardsErrorArguments")
    public <T extends Throwable> void testGetCardsError(int statusCode, Class<T> expectedError) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ICardService service = spy(new CardService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn("Error Message");
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(response);

        assertThrows(expectedError, () -> {
            service.getCards();
        });
    }

    private static Stream<Arguments> getTestGetCardsErrorArguments() {
        return Stream.of(
                Arguments.of(400, BadRequestException.class),
                Arguments.of(401, UnauthorizedException.class),
                Arguments.of(403, ForbiddenException.class),
                Arguments.of(404, NotFoundException.class),
                Arguments.of(500, ServerException.class)
        );
    }

}

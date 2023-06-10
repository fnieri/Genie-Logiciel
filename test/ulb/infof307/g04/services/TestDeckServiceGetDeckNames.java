package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.BadRequestException;
import ulb.infof307.g04.exceptions.api.ForbiddenException;
import ulb.infof307.g04.exceptions.api.NotFoundException;
import ulb.infof307.g04.exceptions.api.ServerException;
import ulb.infof307.g04.exceptions.api.UnauthorizedException;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.models.Deck;

public class TestDeckServiceGetDeckNames {
    private String getDecksJsonResponse;

    @BeforeAll
    static void setUpAll() {
        // FIXME: this sould be done by injecting the cache service into
        // the other services and mocking it.
        CacheService.getInstance().setEnabled(false);
    }

    @BeforeEach
    void setUpEach() throws IOException, URISyntaxException {
        URL file = getClass().getResource("/ulb/infof307/g04/services/getDecks.json");
        getDecksJsonResponse = Files.readString(Path.of(file.toURI()));
    }

    @Test
    public void testGetDeckNames() throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        IDeckService service = spy(new DeckService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(getDecksJsonResponse);
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        List<Deck> decks = service.getDecks();
        assertArrayEquals(Arrays.asList(
                "deck1",
                "deck2"
        ).toArray(), decks.stream().map(Deck::getName).toArray());
    }

    @ParameterizedTest
    @MethodSource("getTestGetDeckNamesErrorArguments")
    public <T extends Throwable> void testGetDeckNamesError(int statusCode, Class<T> expectedError) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        IDeckService service = spy(new DeckService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn("Error Message");
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        assertThrows(expectedError, () -> {
            service.getDecks();
        });
    }

    private static Stream<Arguments> getTestGetDeckNamesErrorArguments() {
        return Stream.of(
                Arguments.of(400, BadRequestException.class),
                Arguments.of(401, UnauthorizedException.class),
                Arguments.of(403, ForbiddenException.class),
                Arguments.of(404, NotFoundException.class),
                Arguments.of(500, ServerException.class)
        );
    }
}

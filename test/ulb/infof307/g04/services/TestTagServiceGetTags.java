package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ulb.infof307.g04.interfaces.services.ITagService;
import ulb.infof307.g04.models.Tag;

public class TestTagServiceGetTags {
    private String getTagsJsonResponse;

    @BeforeAll
    static void setUpAll() {
        // FIXME: this sould be done by injecting the cache service into
        // the other services and mocking it.
        CacheService.getInstance().setEnabled(false);
    }

    @BeforeEach
    void setUpEach() throws IOException, URISyntaxException {
        URL file = getClass().getResource("/ulb/infof307/g04/services/getTags.json");
        getTagsJsonResponse = Files.readString(Path.of(file.toURI()));
    }

    @Test
    public void testGetTags() throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ITagService service = spy(new TagService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(getTagsJsonResponse);
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        List<Tag> tags = service.getTags();
        assertArrayEquals(Arrays.asList(
            new Tag(5, "newtag"),
            new Tag(4, "oldtag")
        ).toArray(), tags.toArray());
    }

    @ParameterizedTest
    @MethodSource("getTestGetTagsErrorArguments")
    public <T extends Throwable> void testGetTagsError(int statusCode, Class<T> expectedError) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ITagService service = spy(new TagService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn("Error Message");
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        assertThrows(expectedError, () -> {
            service.getTags();
        });
    }

    private static Stream<Arguments> getTestGetTagsErrorArguments() {
        return Stream.of(
            Arguments.of(400, BadRequestException.class),
            Arguments.of(401, UnauthorizedException.class),
            Arguments.of(403, ForbiddenException.class),
            Arguments.of(404, NotFoundException.class),
            Arguments.of(500, ServerException.class)
        );
    }
}

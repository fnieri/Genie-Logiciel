package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import java.util.stream.Stream;

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

public class TestTagServiceCreateTag {
    private String createTagJsonResponse;
    
    @BeforeEach
    void setUpEach() throws IOException, URISyntaxException {
        URL file = getClass().getResource("/ulb/infof307/g04/services/createTag.json");
        createTagJsonResponse = Files.readString(Path.of(file.toURI()));
    }

    @Test
    public void testCreateTagWhichDoesNotExist() throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ITagService service = spy(new TagService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(createTagJsonResponse);
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
        doThrow(new ApiException("Hello World !")).when(service).getTagByName(any());

        int tagId = service.createTag("newtag");
        assertEquals(5, tagId);
    }

    @Test
    public void testCreateTagWhichDoesExist() throws IOException, InterruptedException, ApiException {
        ITagService service = spy(new TagService("http://localhost:8000"));
        doReturn(new Tag(4, "oldtag")).when(service).getTagByName(any());

        int tagId = service.createTag("oldtag");
        assertEquals(4, tagId);
    }

    @ParameterizedTest
    @MethodSource("getTestCreateTagErrorArguments")
    public <T extends Throwable> void testCreateTagError(int statusCode, Class<T> expectedError) throws IOException, InterruptedException, ApiException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();
        ITagService service = spy(new TagService("http://localhost:8000", client));

        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn("Error Message");
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);
        doThrow(new ApiException("Hello World !")).when(service).getTagByName(any());

        assertThrows(expectedError, () -> {
            service.createTag("newtag");
        });
    }

    private static Stream<Arguments> getTestCreateTagErrorArguments() {
        return Stream.of(
            Arguments.of(400, BadRequestException.class),
            Arguments.of(401, UnauthorizedException.class),
            Arguments.of(403, ForbiddenException.class),
            Arguments.of(404, NotFoundException.class),
            Arguments.of(500, ServerException.class)
        );
    }
}

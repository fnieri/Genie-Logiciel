package ulb.infof307.g04.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.dtos.User;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.BadRequestException;
import ulb.infof307.g04.exceptions.api.ForbiddenException;
import ulb.infof307.g04.exceptions.api.NotFoundException;
import ulb.infof307.g04.exceptions.api.PasswordMismatchException;
import ulb.infof307.g04.exceptions.api.ServerException;
import ulb.infof307.g04.exceptions.api.UnauthorizedException;
import ulb.infof307.g04.interfaces.services.IUserService;

public class TestUserServiceEditPassword {
    private AppState state = AppState.getInstance();

    @BeforeEach
    void setUp() {
        this.state.setUser(new User(0, "dummy", "dummy"));
        this.state.setAuthToken("Basic dummytoken");
    }

    @Test
    public void testUserEditPassword() throws ApiException, IOException, InterruptedException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();

        when(response.statusCode()).thenReturn(200);
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        IUserService service = new UserService("http://localhost:8000", client);
        service.editPassword("dummy", "dummy", "dummy");

        assertNotNull(state.getAuthToken());
        assertEquals(new User(0, "dummy", "dummy"), state.getUser());
    }

    @Test
    public void testUserEditPasswordPasswordMissmatch() {
        HttpClient client = mock();
        IUserService service = new UserService("http://localhost:8000", client);

        assertThrows(PasswordMismatchException.class, () -> {
            service.editPassword("dummy", "dummy", "different");
        });
    }

    @ParameterizedTest
    @MethodSource("getTestUserEditPasswordErrorArguments")
    public <T extends Throwable> void testUserEditPasswordError(int statusCode, Class<T> expectedError) throws IOException, InterruptedException {
        HttpResponse<String> response = mock();
        HttpClient client = mock();

        when(response.statusCode()).thenReturn(statusCode);
        when(client.send(any(), eq(BodyHandlers.ofString()))).thenReturn(response);

        IUserService service = new UserService("http://localhost:8000", client);
        assertThrows(expectedError, () -> {
            service.editPassword("dummy", "dummy", "dummy");
        });
    }

    private static Stream<Arguments> getTestUserEditPasswordErrorArguments() {
        return Stream.of(
            Arguments.of(400, BadRequestException.class),
            Arguments.of(401, UnauthorizedException.class),
            Arguments.of(403, ForbiddenException.class),
            Arguments.of(404, NotFoundException.class),
            Arguments.of(500, ServerException.class)
        );
    }
}
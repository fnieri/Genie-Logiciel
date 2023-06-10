package ulb.infof307.g04.services;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ulb.infof307.g04.AppState;
import ulb.infof307.g04.dtos.User;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.InvalidCredentialsException;
import ulb.infof307.g04.exceptions.api.PasswordMismatchException;
import ulb.infof307.g04.exceptions.api.UserNotFoundException;
import ulb.infof307.g04.exceptions.api.InvalidOldPasswordException;
import ulb.infof307.g04.exceptions.api.RegistrationErrorException;
import ulb.infof307.g04.interfaces.services.IUserService;

/**
 * Service to handle user related API calls.
 * @see IUserService
 * @see AbstractAPIService
 * @see User
 */
public class UserService extends AbstractAPIService implements IUserService {

    @JsonSerialize
    private record SignUpRequest(String username, String password) {
    }

    @JsonSerialize
    private record EditPasswordRequest(String old_password, String new_password) {
    }

    private final AppState state;

    public UserService(String baseUri) {
        super(baseUri);
        this.state = AppState.getInstance();
    }

    public UserService(String baseUri, HttpClient client) {
        super(baseUri, client);
        this.state = AppState.getInstance();
    }

    /**
     * Set the current user and its token in the AppState.
     * @param username the username of the user
     * @param password the password of the user
     * @param token the token of the user
     * @return the user
     */
    private User setUser(String username, String password, String token) {
        this.state.setAuthToken(token);
        this.state.setUser(new User(0, username, password));
        return this.state.getUser();
    }

    /**
     * Buld the token of the user that wants to sign in.
     * @param username the username of the user
     * @param password the password of the user
     * @return the token
     */
    private String buildToken(String username, String password) {
        return String.format("Basic %s", Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Request the API to sign in the user.
     * @param username the username of the user
     * @param password the password of the user
     * @return the user
     * @throws ApiException If a Django error occurs
     */
    @Override
    public User signIn(String username, String password) throws ApiException {
        String token = this.buildToken(username, password);
        HttpRequest request = this.getRequestBuilder("/api/quiz/tags/", token).GET().build();
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response,
                () -> setUser(username, password, token),
                InvalidCredentialsException::new,
                message -> new ApiException("Unexpected error occurred: " + message));
    }

    /**
     * Get the user with the given id.
     * @param userId the id of the user
     * @return the user
     * @throws UserNotFoundException If the user is not found
     */
    @Override
    public User getUser(int userId) throws UserNotFoundException {
        User user = this.state.getUser();
        if (user == null) throw new UserNotFoundException("User not found.");
        return user;
    }


    /**
     * Edit the password of the current user.
     * @param oldPassword the old password of the user
     * @param newPassword the new password of the user
     * @param confirmPassword the confirmation of the new password
     * @throws ApiException If a Django error occurs
     */
    @Override
    public void editPassword(String oldPassword, String newPassword, String confirmPassword) throws ApiException {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("Password does not match the confirm password.");
        }


        ObjectMapper obj = new ObjectMapper();
        String body = null;
        try {
            body = obj.writeValueAsString(new EditPasswordRequest(oldPassword, newPassword));
        } catch (JsonProcessingException e) {
            throw new ApiException("Unexpected error occurred: " + e.getMessage());
        }
        HttpRequest request = this.getRequestBuilder("/api/auth/change-password/", this.state.getAuthToken()).PUT(BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = sendRequest(request);

        String token = this.buildToken(this.state.getUser().username(), newPassword);
        handleResponse(response,
                () -> this.setUser(this.state.getUser().username(), newPassword, token),
                InvalidOldPasswordException::new,
                message -> new ApiException("Unexpected error occurred: " + message));
    }


    /**
     * Handle the response of the API.
     * @param response the response of the API
     * @param successAction the action to do if the response is successful
     * @param errorCase the action to do if the response is an error
     * @param defaultErrorCase the action to do if the response is an unexpected error
     * @return the result of the action
     * @param <T> the type of the result
     * @throws ApiException If a Django error occurs
     */
    private <T> T handleResponse(HttpResponse<String> response, Supplier<T> successAction, Function<String, ApiException> errorCase, Function<String, ApiException> defaultErrorCase) throws ApiException {
        switch (response.statusCode()) {
            case 200, 201 -> {
                return successAction.get();
            }
            case 400, 403 -> {
                throw errorCase.apply(response.body());
            }
            default -> {
                throw defaultErrorCase.apply(response.body());
            }
        }
    }

    /**
     * Sign up the user.
     * @param username the username of the user
     * @param password the password of the user
     * @param confirmPassword the confirmation of the password
     * @return the user
     * @throws ApiException If a Django error occurs or if the password does not match the confirmation
     */
    @Override
    public User signUp(String username, String password, String confirmPassword) throws ApiException {
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException("Password does not match the confirm password.");
        }


        String token = this.buildToken(username, password);
        ObjectMapper obj = new ObjectMapper();
        String body = null;
        try {
            body = obj.writeValueAsString(new SignUpRequest(username, password));
        } catch (JsonProcessingException e) {
            throw new ApiException("Unexpected error occurred: " + e.getMessage());
        }
        HttpRequest request = this.getRequestBuilder("/api/auth/register/")
                .POST(BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = sendRequest(request);

        return handleResponse(response,
                () -> setUser(username, password, token),
                RegistrationErrorException::new,
                message -> new ApiException("Unexpected error occurred: " + message));
    }
}

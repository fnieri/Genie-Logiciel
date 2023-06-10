package ulb.infof307.g04.interfaces.services;

import ulb.infof307.g04.dtos.User;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.api.UserNotFoundException;

public interface IUserService {
    User signIn(String username, String password) throws ApiException;
    User getUser(int userId) throws UserNotFoundException;
    void editPassword(String oldPassword, String newPassword, String confirmPassword) throws ApiException;
    User signUp(String username, String password, String confirmPassword) throws ApiException;
}

package ulb.infof307.g04;

import ulb.infof307.g04.dtos.User;

/**
 * A global object to keep track of the application state and info.
 * Follows the Singleton pattern and is accessible app-wide.
 * Useful to store information about the user.
 */
public class AppState {
    private static AppState instance;

    private int userId;
    private User user;

    private String authToken;

    private AppState() {}

    public static AppState getInstance() {
        if (instance == null)
        {
            instance = new AppState();
        }

        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id)
    {
        userId = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

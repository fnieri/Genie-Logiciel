package ulb.infof307.g04.interfaces.controller;

public interface ISettingsController extends IController {

    /**
     * Edit the current user's password
     *
     * @param oldPassword - previous password string
     * @param newPassword - new password string 
     * @param confirmPassword - confirmation of new password string
     */
    void editPassword(String oldPassword, String newPassword, String confirmPassword);
}
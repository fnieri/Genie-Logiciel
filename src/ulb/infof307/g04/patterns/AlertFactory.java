package ulb.infof307.g04.patterns;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import ulb.infof307.g04.App;

public class AlertFactory {

    /**
     * Method used to create an alert to show on the screen
     *
     * @param type        Alert type
     * @param title       The title to be shown
     * @param headerText  The header text indicating the main content to be displayed
     * @param contentText Content text, usually a longer version of the header text or an exception log
     * @return The alert requested
     */

    public Alert makeAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        //https://stackoverflow.com/questions/53105080/add-scrollbar-into-alert
        Alert alert = makeBaseAlert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(null);

        //Set scrollable area when user clicks view more
        TextArea area = new TextArea(contentText);
        area.setWrapText(true);
        area.setEditable(false);


        alert.getDialogPane().setExpandableContent(area);

        return alert;
    }

    public Dialog<String> makeTextInputDialog(String title, String headerText, String contentText, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);
        dialog.setGraphic(null);

        dialog.getDialogPane().getStylesheets().add(getClass().getResource(App.cssPath).toExternalForm());
        dialog.getDialogPane().getStyleClass().add("myDialog");
        return dialog;
    }

    public Alert makeBaseAlert(Alert.AlertType type) {
        Alert alert = new Alert(type);

        alert.getDialogPane().getStylesheets().add(getClass().getResource(App.cssPath).toExternalForm());
        alert.getDialogPane().getStyleClass().add("myDialog");

        return alert;
    }
}

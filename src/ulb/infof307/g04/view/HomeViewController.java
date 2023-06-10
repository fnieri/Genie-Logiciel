package ulb.infof307.g04.view;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.interfaces.view.IAppViewController;


/**
 * Parent view controller for the Home view (declared in home.fxml)
 * The Home view contains elements that don't change throughout the Scene (e.g. the nav bar).
 * It features "sub-views" that add visuals to the FXML file and instantiates related 
 * sub-controllers, based on where the User is in the program.
 *
 * @see ulb.infof307.g04.controllers.ProfileController
 * @see ulb.infof307.g04.controllers.SettingsController
 */
public class HomeViewController extends AbstractViewController implements Initializable {
    @FXML StackPane menus;
    @FXML Button navBtnHome;
    @FXML Button navBtnStatistics;
    @FXML Button navBtnSettings;
    @FXML Button navBtnStore;

    public HomeViewController(IAppViewController app, HashMap<String, Object> params) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Callback for the buttons on the navigation bar.
     * Changes the scene accordingly.
     *
     * @param event the click event that triggered the callback
     * 
     */
    @FXML
    public void handleNavButton(ActionEvent event) {
        if (event.getSource() == navBtnHome) {
            this.switchView(EPages.PROFILE);

        } else if (event.getSource() == navBtnStatistics){
            this.switchView(EPages.STATISTICS);

        } else if (event.getSource() == navBtnSettings) {
            this.switchView(EPages.SETTINGS);
        } else if (event.getSource() == navBtnStore){
            this.switchView(EPages.STORE);
        }
    }
}

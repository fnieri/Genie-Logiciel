package ulb.infof307.g04;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.factory.*;
import ulb.infof307.g04.factory.CardServiceFactory;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.QuizResultServiceFactory;
import ulb.infof307.g04.factory.StoreDeckServiceFactory;
import ulb.infof307.g04.factory.TagServiceFactory;
import ulb.infof307.g04.factory.UserServiceFactory;
import ulb.infof307.g04.interfaces.view.IAppViewController;
import ulb.infof307.g04.patterns.AlertFactory;
import ulb.infof307.g04.services.CardService;
import ulb.infof307.g04.services.DeckService;
import ulb.infof307.g04.services.QuizResultService;
import ulb.infof307.g04.services.StoreDeckService;
import ulb.infof307.g04.services.TagService;
import ulb.infof307.g04.services.UserService;
import ulb.infof307.g04.services.StatisticService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class representing an instance of the Flashcards application.
 * <p>
 * It is instantiated in the Main class of the program.
 *
 * @see ulb.infof307.g04.Main
 */
public class App extends Application implements IAppViewController {
    public static final String cssPath = "/ulb/infof307/g04/ui/base.css";
    private static final String APP_TITLE = "QuickMem";
    private static final double MIN_WIDTH = 900;
    private static final double MIN_HEIGHT = 600;

    private Stage primaryStage;

    public App() {
        // NOTE: Order is important here
        String baseUri = "https://flashcards.ulb.lucapalmi.com";
        UserServiceFactory.setInstance(new UserService(baseUri));
        TagServiceFactory.setInstance(new TagService(baseUri));
        CardServiceFactory.setInstance(new CardService(baseUri));
        DeckServiceFactory.setInstance(new DeckService(baseUri));
        QuizResultServiceFactory.setInstance(new QuizResultService(baseUri));
        StoreDeckServiceFactory.setInstance(new StoreDeckService(baseUri));
        StatisticServiceFactory.setInstance(new StatisticService(baseUri));
    }

    /**
     * Starts the JavaFX application and sets its global parameters.
     *
     * @param stage the beginning top-level container of JavaFX
     */
    @Override
    public void start(Stage stage){
        primaryStage = stage;
        handleViewChange(EPages.SIGN_IN, null);
        stage.setTitle(APP_TITLE);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.show();
    }

    @Override
    public Window getWindow() {
        return primaryStage.getScene().getWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showLoadingError(Exception e) {
        AlertFactory alertFactory = new AlertFactory();
        Alert alert = alertFactory.makeAlert(Alert.AlertType.ERROR, "Error loading scenes",
                "An error has occurred while loading a scene, contact the developers!",
                Arrays.toString(e.getStackTrace()));
        alert.showAndWait();
    }

    @Override
    public void handleViewChange(EPages page, HashMap<String, Object> params) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(getFxmlPathForPage(page)));

        loader.setControllerFactory(controllerClass -> {
            try {
                return controllerClass.getDeclaredConstructor(IAppViewController.class, HashMap.class).newInstance(this, params);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                showLoadingError(e);
                return null;
            }
        });

        try {
            Parent parent = loader.load();
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(parent);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(parent);
            }
        } catch (IOException | IllegalStateException e) {
            showLoadingError(e);
        }
    }

    private String getFxmlPathForPage(EPages page) {
        return switch (page) {
            case SIGN_IN -> "/ulb/infof307/g04/ui/login/login.fxml";
            case SIGN_UP -> "/ulb/infof307/g04/ui/signup/signup.fxml";
            case PROFILE -> "/ulb/infof307/g04/ui/profile/layout.fxml";
            case STORE ->  "/ulb/infof307/g04/ui/store/layout.fxml";
            case SETTINGS -> "/ulb/infof307/g04/ui/settings/layout.fxml";
            case EDIT_DECK -> "/ulb/infof307/g04/ui/editDeck/layout.fxml";
            case STATISTICS -> "/ulb/infof307/g04/ui/statistics/layout.fxml";
            case LEARNING_MODE -> "/ulb/infof307/g04/ui/learning/layout.fxml";

            default -> throw new IllegalArgumentException("Invalid page");
        };
    }
}
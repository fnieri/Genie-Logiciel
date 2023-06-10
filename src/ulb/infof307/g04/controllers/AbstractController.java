package ulb.infof307.g04.controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import ulb.infof307.g04.interfaces.controller.IController;
import ulb.infof307.g04.interfaces.view.IViewController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Abstract class for the different controllers of the application.
 * <p>
 * Every controller inherits from this class that provides basic 
 * controller functionalities.
 */
public abstract class AbstractController implements IController {
    protected IViewController view;
    protected final Executor executor = Executors.newFixedThreadPool(5);


    /**
     * Constructor of the AbstractController class.
     *
     * @param view the view controller that the controller interacts with.
     */
    AbstractController(IViewController view) {
        this.view = view;
    }

    /**
     * Initializes the controller. This is used as an alternative way to make 
     * initialization procedures to the constructor, to ensure the FXML has 
     * been already properly loaded.
     */
    @Override
    public void initialize() {
    }

    /**
     * Providing a way to perform a customer task asynchronously based on a 
     * supplier task
     *
     * @param task - the task providing results 
     * @param consumer - the task consuming results 
     */
    protected <T> void runAsync(Supplier<T> task, Consumer<T> onSuccess) {
        runAsync(task, onSuccess, "", "An error occurred");
    }

    /**
     * Providing a way to perform a customer task asynchronously based on a 
     * supplier task, with a progress msg to be displayed during the supplier task.
     *
     * @param task - the task providing results 
     * @param consumer - the task consuming results 
     * @param progressMessage - the string msg to be displayed
     */
    protected <T> void runAsync(Supplier<T> task, Consumer<T> onSuccess, String progressMessage) {
        runAsync(task, onSuccess, progressMessage, "An error occurred");
    }

    /**
     * Providing a way to perform a customer task asynchronously based on a 
     * supplier task, with a progress msg to be displayed during the supplier task and 
     * an error msg if it fails.
     *
     * @param task - the task providing results 
     * @param consumer - the task consuming results 
     * @param progressMessage - the string msg to show progress
     * @param errorMessage - the string msg to show error
     */
    protected <T> void runAsync(Supplier<T> task, Consumer<T> onSuccess, String progressMessage, String errorMessage) {
        view.showProgress(progressMessage);
        CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor).whenComplete((result, ex) -> {
            Platform.runLater(() -> {
                view.hideProgress();
                if (ex != null) {
                    Throwable throwable = (ex instanceof CompletionException) ? ex.getCause() : ex;
                    Consumer<Throwable> onError =
                            (throwable instanceof Exception) ?
                                    e -> showAlertForException((Exception) e, errorMessage) :
                                    e -> showAlertForException(e.getMessage(), errorMessage, "");
                    onError.accept(throwable);
                } else {
                    onSuccess.accept(result);
                }
            });
        });
    }


    /**
     * Providing a way to perform a task asynchronously based on another 
     * task, both without a return, with a progress msg to be displayed during 
     * the supplier task and an error msg if it fails.
     *
     * @param task - the task to be ran first
     * @param consumer - the task to be ran second
     * @param progressMessage - the string msg to show progress
     * @param errorMessage - the string msg to show error
     */
    protected void runAsync(Runnable task, Runnable onSuccess, String progressMessage, String errorMessage) {
        runAsync(() -> {
            task.run();
            return null;
        }, result -> onSuccess.run(), progressMessage, errorMessage);
    }


    /**
     * Method to show an error alert for the given exception with the specified title.
     *
     * @param e     the exception to display in the alert.
     * @param title the title of the alert.
     */
    protected void showAlertForException(Exception e, String title) {
        showAlertForException(title, e.getMessage(), "");
    }

    /**
     * Method to show an error alert with the specified title, header and content.
     *
     * @param title   the title of the alert.
     * @param header  the header of the alert.
     * @param content the content of the alert.
     */
    protected void showAlertForException(String title, String header, String content) {
        view.showAlert(Alert.AlertType.ERROR, title, header, content);
    }
}
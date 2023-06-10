package ulb.infof307.g04;

/**
 * Main class of the Flashcards program.
 */
public class Main {

    public static void main(String[] args) {
        // Set the logging configuration file path
        System.setProperty("java.util.logging.config.file", "resources/logging.properties");

        App.main(args);
    }
}

package ulb.infof307.g04.widgets.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ulb.infof307.g04.App;

import java.util.ArrayList;
import java.util.Objects;

public class DeckCreationDialog extends Dialog<ArrayList<String>> {

    private final TextField deckName = new TextField();
    private final TextField deckTags = new TextField();

    public DeckCreationDialog() {
        Label nameLabel = new Label("Deck Name");
        Label tagsLabel = new Label("Deck tags (separated by commas)");

        VBox vbox = new VBox(
                nameLabel, deckName,
                tagsLabel, deckTags
        );

        vbox.setSpacing( 10.0d );

        DialogPane dp = getDialogPane();

        nameLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        nameLabel.getStyleClass().add("textLabel");

        tagsLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        tagsLabel.getStyleClass().add("textLabel");

        dp.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        dp.getStyleClass().add("myDialog");

        setTitle("Create Deck");

        setResultConverter( this::formResult );

        ButtonType bt = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(bt, ButtonType.CANCEL);
        dp.setContent(vbox);
    }

    private ArrayList<String> formResult(ButtonType bt) {
        ArrayList<String> deckNameTheme = new ArrayList<>();
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            deckNameTheme.add(deckName.getText());
            deckNameTheme.add(deckTags.getText());
        }
        return deckNameTheme;
    }
}
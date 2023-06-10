package ulb.infof307.g04.widgets.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ulb.infof307.g04.App;

import java.util.ArrayList;
import java.util.Objects;

public class DeckDeletionDialog extends Dialog<ArrayList<String>> {

    private final TextField deckName = new TextField();

    public DeckDeletionDialog() {
        Label nameLabel = new Label("To delete the chosen deck, please confirm by writing the name of the said deck");

        VBox vbox = new VBox(
                nameLabel, deckName
        );

        vbox.setSpacing( 10.0d );



        DialogPane dp = getDialogPane();
        setTitle("Deck Deletion");

        nameLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        nameLabel.getStyleClass().add("textLabel");

        dp.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        dp.getStyleClass().add("myDialog");

        setResultConverter( this::formResult );

        ButtonType bt = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(bt, ButtonType.CANCEL);
        dp.setContent(vbox);
    }

    private ArrayList<String> formResult(ButtonType bt) {
        ArrayList<String> toDeleteDeckName = new ArrayList<>();
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            toDeleteDeckName.add(deckName.getText());
        }
        return toDeleteDeckName;
    }
}
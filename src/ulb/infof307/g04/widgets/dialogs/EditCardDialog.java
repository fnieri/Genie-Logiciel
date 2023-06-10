package ulb.infof307.g04.widgets.dialogs;

import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import ulb.infof307.g04.App;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.BlanksCard;
import ulb.infof307.g04.models.MCQCard;
import ulb.infof307.g04.models.OpenAnswerCard;
import ulb.infof307.g04.patterns.CardFactory;

import javafx.scene.paint.Color;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.exceptions.CardLengthException;
import ulb.infof307.g04.parsers.LatexParser;
import ulb.infof307.g04.patterns.AlertFactory;
import org.scilab.forge.jlatexmath.*;
import ulb.infof307.g04.patterns.CardVisitor;

import java.util.ArrayList;
import java.util.Objects;


public class EditCardDialog extends Dialog<ICard>  implements CardVisitor {

    private final TextArea questionTextArea = new TextArea();
    private final TextArea answerTextArea = new TextArea();
    private final ChoiceBox<String> questionFormat = new ChoiceBox<>();
    private final ChoiceBox<String> answerFormat = new ChoiceBox<>();
    private final ICard card;
    private final int maxLength = 250;
    Label questionLabel = new Label("Question");
    Label answerLabel = new Label("Answer");
    Label questionLength = new Label();
    Label answerLength = new Label();
    CardFactory cardFactory = new CardFactory();


    public EditCardDialog(CardType cardType) {
        // NOTE: This is used when creating a new card
        CardFactory cardFactory = new CardFactory();
        if (cardType.isType(CardType.OAR)) {
            card = cardFactory.makeCard(0, 0, "", "", CardType.PLAIN_TEXT, CardType.PLAIN_TEXT, CardMasteryLevels.NOT_YET_LEARNED);
        }
        else {
            card = cardFactory.makeCard(0, 0, "", new ArrayList<>(), CardType.PLAIN_TEXT, cardType, CardMasteryLevels.NOT_YET_LEARNED);
        }
        showInitialCardData(card);
        this.setupView();
        this.setTitle("Add Card");
    }

    public EditCardDialog(ICard card) {
        // NOTE: This is used when editing a card
        this.card = card;
        showInitialCardData(card);
        this.setupView();
        this.setTitle("Edit Card");
    }


    /**
     * Main Method of the class that creates a pop-up window to create or edit a card in the EditDeckView
     */
    public void setupView() {

        this.questionTextArea.setPrefRowCount(5);
        this.answerTextArea.setPrefRowCount(5);

        setupFormatChoiceBox(this.questionFormat);
        setupFormatChoiceBox(this.answerFormat);

        card.accept(this);

    }

    /**
     * Method used to include a choice box for the card types in the pop-up window when creating a new card
     */
    private void setupFormatChoiceBox(ChoiceBox<String> choiceBox) {
        choiceBox.getItems().addAll(
            CardType.PLAIN_TEXT.toViewString(),
            CardType.HTML.toViewString(),
            CardType.LATEX.toViewString()
        );
        choiceBox.setValue(CardType.PLAIN_TEXT.toViewString());
    }

    /**
     * Method used to write and update the character count/maximum in the pop-up window
     */
    private void listenerMaxLengthLabel(Label toUpdate, TextArea textToListen, Label labelName) {
        toUpdate.textProperty().bind(Bindings.createStringBinding(() -> {
            String lengthText = labelName.getText() + " length: " + textToListen.getLength() + "/250";
            if (textToListen.getLength() > maxLength) {
                toUpdate.setTextFill(Color.RED);
            } else {
                toUpdate.setTextFill(Color.WHITE);
            }
            return lengthText;
            }, textToListen.textProperty()));
    }

    /**
     * Show the initial card data in the dialog box
     * @param card The card to be edited
     */
    private void showInitialCardData(ICard card) {
        questionTextArea.setText(card.getQuestion());
        answerTextArea.setText(card.getAnswer());
    }

    private int getQuestionLength(){
        return questionTextArea.getLength();
    }

    private int getAnswerLength(){
        return answerTextArea.getLength();
    }

    /**
     * Check if the card length is valid
     */
    public void checkCardLengthValidity(){
        try {
            if (getQuestionLength() > maxLength) {
                throw new CardLengthException("Question");
            }
            if (getAnswerLength() > maxLength) {
                throw new CardLengthException("Answer");
            }
        } catch (CardLengthException e){
            showCardLengthError(e);
        }
    }

    /**
     * Exception method to ensure proper LaTex parsing
     * @param e exception call
     */
    private void showParsingError(Exception e) {
        String message = "The latex code you entered is invalid";
        AlertFactory alertFactory = new AlertFactory();
        alertFactory.makeAlert(Alert.AlertType.WARNING, "Parsing error", message, e.getMessage()).show();;
    }

    /**
     * Exception method used to ensure the questions/answers aren't longer than 250 characters
     * @param e exception call
     */
    private void showCardLengthError(Exception e) {
        String message = "The " + e.getMessage() + " part contains too much caracters";
        AlertFactory alertFactory = new AlertFactory();
        alertFactory.makeAlert(Alert.AlertType.WARNING, "Too much caracters", message, e.getMessage()).show();;
    }

    /**
     * Method used to check if the latex is valid
     * @param text The question to be checked
     */
    private void checkLatexValidity(String text) throws ParseException {
        LatexParser latexParser = new LatexParser();
        latexParser.parseEquation(text);
    }

    /**
     * Method used to form the result of the dialog box as an open answer of the corresponding types card
     * @param bt The button type
     * @return The card
     */
    private ICard OARGetResult(ButtonType bt) {
        // NOTE: In this case we must handle HTML & LATEX
        ICard updatedCard = null;
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            try {
                CardType questionFormatting = CardType.fromViewString(this.questionFormat.getValue());
                CardType answerFormatting = CardType.fromViewString(this.answerFormat.getValue());
                if (questionFormatting == CardType.LATEX) {
                    checkLatexValidity(questionTextArea.getText());
                }
                if (answerFormatting == CardType.LATEX) {
                    checkLatexValidity(answerTextArea.getText());
                }

                updatedCard = cardFactory.makeCard(0, 0, questionTextArea.getText(), answerTextArea.getText(), questionFormatting, answerFormatting, CardMasteryLevels.NOT_YET_LEARNED);
            } catch (ParseException e) {
                showParsingError(e);
            }
        }
        return updatedCard;
    }

    /**
     * Method used to form the result of the dialog box as a Multiple Choice Answers card
     * @param bt The button type
     * @return The card
     */
    private ICard MCQGetResult(ButtonType bt) {
        // NOTE: This card only contains PLAIN TEXT
        ICard updatedCard = null;
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            updatedCard = cardFactory.makeCard(0, 0, questionTextArea.getText(),
                    answerTextArea.getText(),
                    CardType.PLAIN_TEXT, CardType.MCQ, CardMasteryLevels.NOT_YET_LEARNED);
        }
        return updatedCard;
    }

    /**
     * Method used to form the result of the dialog box as a Blanks-to-fill card
     * @param bt The button type
     * @return The card
     */
    private ICard BLNKGetResult(ButtonType bt) {
        // NOTE: This card only contains PLAIN TEXT
        ICard updatedCard = null;
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            updatedCard = cardFactory.makeCard(0, 0, questionTextArea.getText(),
                    answerTextArea.getText(),
                    CardType.PLAIN_TEXT, CardType.BLNK, CardMasteryLevels.NOT_YET_LEARNED);
        }
        return updatedCard;
    }

    /**
     * Method used when visiting openAnswerCard object
     * @param openAnswerCard an open answer type card
     */
    @Override
    public void visit(OpenAnswerCard openAnswerCard) {

        VBox vbox = createVbox();
        DialogPane dp = createPane();

        setResultConverter(this::OARGetResult);

        setButtons(vbox, dp);
    }

    /**
     * Method used when visiting MCQCard object
     * @param mcqCard a multiple choice card type card
     */
    @Override
    public void visit(MCQCard mcqCard) {
        
        this.questionFormat.setVisible(false);
        this.answerFormat.setVisible(false);

        VBox vbox = createVbox();
        DialogPane dp = createPane();
        
        setResultConverter(this::MCQGetResult);

        setButtons(vbox, dp);
    }

    /**
     * Method used when visiting BlanksCard object
     * @param blanksCard a blanks-to-fill type card
     */
    @Override
    public void visit(BlanksCard blanksCard) {

        this.questionFormat.setVisible(false);
        this.answerFormat.setVisible(false);

        VBox vbox = createVbox();
        DialogPane dp = createPane();

        setResultConverter(this::BLNKGetResult);

        setButtons(vbox, dp);
    }

    /**
     * Method used for graphics interface
     * @return dialog pane of the pop-up window
     */
    private DialogPane createPane() {
        DialogPane dp = getDialogPane();

        questionLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        questionLabel.getStyleClass().add("myDialog");

        answerLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        answerLabel.getStyleClass().add("myDialog");

        dp.getStylesheets().add(Objects.requireNonNull(getClass().getResource(App.cssPath)).toExternalForm());
        dp.getStyleClass().add("myDialog");
        return dp;
    }

    /**
     * Method used for graphics interface
     * @return the vbox containing the pane for the pop-up window
     */
    @NotNull
    private VBox createVbox() {
        HBox questionHeader = new HBox(questionLabel, questionFormat);
        questionHeader.setSpacing(10.0d);
        HBox answerHeader = new HBox(answerLabel, answerFormat);
        answerHeader.setSpacing(10.0d);

        VBox vbox = new VBox(
                questionHeader, questionTextArea,
                questionLength,
                answerHeader, answerTextArea,
                answerLength
        );
        vbox.setSpacing(10.0d);

        listenerMaxLengthLabel(questionLength, questionTextArea, questionLabel);
        listenerMaxLengthLabel(answerLength, answerTextArea, answerLabel);
        return vbox;
    }


    /**
     * Method used for graphics interface. Creates the pop-up buttons
     *
     */
    private static void setButtons(VBox vbox, DialogPane dp) {
        ButtonType bt = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(bt, ButtonType.CANCEL);
        dp.setContent(vbox);
    }
}



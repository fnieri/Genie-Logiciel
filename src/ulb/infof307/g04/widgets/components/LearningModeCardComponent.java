package ulb.infof307.g04.widgets.components;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import ulb.infof307.g04.enums.CardMasteryLevels;
import ulb.infof307.g04.patterns.CardVisitor;
import ulb.infof307.g04.models.BlanksCard;
import ulb.infof307.g04.models.MCQCard;
import ulb.infof307.g04.models.OpenAnswerCard;

import java.io.IOException;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class LearningModeCardComponent extends CardComponent implements CardVisitor {
    private CardComponent.CardComponentEventHandlerWithReturn<String, Boolean> onAnswerHandler;
    private CardComponent.CardComponentEventHandler<ActionEvent> onGoToPrevCardHandler;
    private CardComponent.CardComponentEventHandler<ActionEvent> onFlipCardHandler;
    private CardComponent.CardComponentEventHandler<ActionEvent> onGoToNextCardHandler;
    private CardComponent.CardComponentEventHandler<CardMasteryLevels> onCardSetMasteryHandler;

    @FXML
    Text pagesText;
    @FXML
    Button prevBtn;
    @FXML
    Button flipBtn;
    @FXML
    Button nextBtn;
    @FXML
    Text askknowledge;
    @FXML
    Button veryGoodBtn;
    @FXML
    Button goodBtn;
    @FXML
    Button averageBtn;
    @FXML
    Button badBtn;
    @FXML
    Button veryBadBtn;

    private Node answerNode;

    public LearningModeCardComponent() throws IOException {
        super(null);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ulb/infof307/g04/ui/components/learningModeCardComponent/layout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        this.setSpacing(16);
        this.setAlignment(Pos.CENTER);
        this.refresh();
    }


    public void onAnswerEvent(CardComponent.CardComponentEventHandlerWithReturn<String, Boolean> handler) {
        this.onAnswerHandler = handler;
    }

    public void onGoToPrevCardEvent(CardComponent.CardComponentEventHandler<ActionEvent> handler) {
        this.onGoToPrevCardHandler = handler;
    }

    public void onFlipCardEvent(CardComponent.CardComponentEventHandler<ActionEvent> handler) {
        this.onFlipCardHandler = handler;
    }

    public void onGoToNextCardEvent(CardComponent.CardComponentEventHandler<ActionEvent> handler) {
        this.onGoToNextCardHandler = handler;
    }

    public void onCardSetMasteryEvent(CardComponent.CardComponentEventHandler<CardMasteryLevels> handler) {
        this.onCardSetMasteryHandler = handler;
    }


    public void setFlipButtonVisible(boolean visible) {
        this.flipBtn.setVisible(visible);
    }

    public void setFlipButtonDisable(boolean disabled) {
        this.flipBtn.setDisable(disabled);
    }

    public void setNavButtonsVisible(boolean isPrevBtnVisible, boolean isNextBtnVisible) {
        this.prevBtn.setVisible(isPrevBtnVisible);
        this.nextBtn.setVisible(isNextBtnVisible);
    }

    public void setNavButtonsDisable(boolean isPrevBtnDisabled, boolean isNextBtnDisabled) {
        this.prevBtn.setDisable(isPrevBtnDisabled);
        this.nextBtn.setDisable(isNextBtnDisabled);
    }

    public void setPages(int current, int max) {
        this.pagesText.setText(String.format("%d/%d", current, max));
    }

    public void setCorrectAnswerVisible(boolean visible) {
        if (this.answerNode != null) {
            this.answerNode.setVisible(visible);
        }
    }

    public void setPerformanceTypeButtonVisible(boolean visible) {
        askknowledge.setVisible(visible);
        veryGoodBtn.setVisible(visible);
        goodBtn.setVisible(visible);
        averageBtn.setVisible(visible);
        badBtn.setVisible(visible);
        veryBadBtn.setVisible(visible);
    }


    @Override
    protected void refresh() {
        this.cardDetailsBox.getChildren().clear();
        if (this.card != null) {
            this.card.accept(this);
        }
    }

    private boolean triggerOnAnswerEvent(String answer) {
        if (this.onAnswerHandler != null) {
            return this.onAnswerHandler.handle(answer);
        }
        return false;
    }

    private void addAnswerTextField() {
        // NOTE: The user has to input the answer in a TextField. This field
        // is created dynamically here. Once the user presses the ENTER key, 
        // the OnAnswerEvent is triggered.
        VBox container = new VBox();
        container.setSpacing(10);
        container.setPadding(new Insets(10));
        TextField answerField = new TextField();
        answerField.setPromptText("Response");
        answerField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String answer = answerField.getText();
                this.triggerOnAnswerEvent(answer);
            }
        });
        container.getChildren().add(answerField);
        cardDetailsBox.getChildren().add(container);
    }

    @Override
    Node addHTMLTextCard(String value) {
        Document doc = Jsoup.parse(value);
        WebView webView = new WebView();
        WebEngine webViewEngine = webView.getEngine();
        webViewEngine.loadContent(doc.html());
        webViewEngine.setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("/ulb/infof307/g04/ui/studyView.css")).toExternalForm());
        cardDetailsBox.getChildren().addAll(webView);
        VBox.setVgrow(webView, Priority.ALWAYS);
        return webView;
    }

    @Override
    public void visit(MCQCard mcqCard) {
        addPlainTextComponent(mcqCard.getQuestion(), "card-question");
        this.answerNode = addPlainTextComponent(mcqCard.getCorrectAnswer(), "card-answer");

        // NOTE: This adds a list of ToggleButton from which the user can select.
        // If the user select the correct answer, the button turns green. Otherwise,
        // the button will turn red.
        List<String> randomizedChoices = new ArrayList<>(mcqCard.getAnswers());
        Collections.shuffle(randomizedChoices);
        HBox optionsBox = new HBox(10);
        ToggleGroup group = new ToggleGroup();
        for (String choice : randomizedChoices) {
            ToggleButton button = new ToggleButton(choice);
            button.setOnAction(clickEvent -> {
                if (button.isSelected()) {
                    boolean res = this.triggerOnAnswerEvent(choice);

                    if (res) {
                        button.getStyleClass().add("good-answer");
                    } else {
                        button.getStyleClass().add("wrong-answer");
                    }
                } else {
                    button.getStyleClass().add("answer-btn");
                }

                // Wait for 1 second and then reset button color
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> button.getStyleClass().add("answer-btn"));
                pause.play();
            });
            button.setToggleGroup(group);
            optionsBox.getChildren().add(button);
        }
        optionsBox.setAlignment(Pos.CENTER);
        cardDetailsBox.getChildren().add(optionsBox);
    }

    @Override
    public void visit(OpenAnswerCard openAnswerCard) {
        String question = openAnswerCard.getQuestion();
        String answer = openAnswerCard.getAnswer();
        addTextComponent(question, "card-question", openAnswerCard.getQuestionType());
        this.answerNode = addTextComponent(answer, "card-answer", openAnswerCard.getAnswerType());
        addAnswerTextField();
    }

    @Override
    public void visit(BlanksCard blanksCard) {
        VBox container = new VBox();
        container.setSpacing(10);
        container.setPadding(new Insets(10));
        List<String> questionList = Arrays.asList(blanksCard.getQuestion().split(";"));
        List<String> answerList = blanksCard.getAnswers();
        String answerCard = String.join(";", answerList);

        TextField answerFieldBlnk;

        for (int i = 0; i < questionList.size() - 1; i++) {
            Label questionLabel = new Label(questionList.get(i));
            answerFieldBlnk = new TextField();
            answerFieldBlnk.setPromptText("Response");
            container.getChildren().addAll(questionLabel, answerFieldBlnk);
            handleKeyPressed(answerFieldBlnk);
        }

        container.getChildren().add(new Label(questionList.get(questionList.size() - 1)));

        cardDetailsBox.getChildren().add(container);
        this.answerNode = addTextComponent(answerCard, "card-answer", blanksCard.getAnswerType());
    }


    private void handleKeyPressed(TextField answerFieldBlnk) {
        answerFieldBlnk.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                answerFieldBlnk.setText("");
                handleFlipCard(null);
                answerFieldBlnk.setBorder(null);
            }
        });
    }


    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    @FXML
    public void handleGotoPrevQuestion(ActionEvent event) {
        if (this.onGoToPrevCardHandler != null) {
            this.onGoToPrevCardHandler.handle(event);
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    @FXML
    public void handleFlipCard(ActionEvent event) {
        if (this.onFlipCardHandler != null) {
            this.onFlipCardHandler.handle(event);
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    @FXML
    public void handleGotoNextQuestion(ActionEvent event) {
        if (this.onGoToNextCardHandler != null) {
            this.onGoToNextCardHandler.handle(event);
        }
    }

    /**
     * @note Although this method is not used in intellij, it is used in fxml
     */
    @FXML
    public void handleMasteryButton(ActionEvent event) {
        CardMasteryLevels mastery = CardMasteryLevels.NOT_YET_LEARNED;
        Button btn = (Button) event.getSource();
        if (btn.equals(veryBadBtn)) {
            mastery = CardMasteryLevels.VERY_BAD;
        } else if (btn.equals(badBtn)) {
            mastery = CardMasteryLevels.BAD;
        } else if (btn.equals(averageBtn)) {
            mastery = CardMasteryLevels.MODERATE;
        } else if (btn.equals(goodBtn)) {
            mastery = CardMasteryLevels.GOOD;
        } else if (btn.equals(veryGoodBtn)) {
            mastery = CardMasteryLevels.VERY_GOOD;
        } else {
            // this else block is not supposed to happen. (Or should we ignore this ?).
            return;
        }

        if (this.onCardSetMasteryHandler != null) {
            this.onCardSetMasteryHandler.handle(mastery);
        }
    }
}

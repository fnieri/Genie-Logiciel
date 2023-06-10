package ulb.infof307.g04.widgets.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ulb.infof307.g04.models.BlanksCard;
import ulb.infof307.g04.models.MCQCard;
import ulb.infof307.g04.models.OpenAnswerCard;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.patterns.CardVisitor;

import java.io.IOException;
import java.util.List;

public class EditDeckCardComponent extends CardComponent implements CardVisitor {
    private CardComponent.CardComponentEventHandler<Integer> onEditClickHandler;
    private CardComponent.CardComponentEventHandler<Integer> onDeleteClickHandler;

    public EditDeckCardComponent(ICard card) throws IOException {
        super(card);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ulb/infof307/g04/ui/components/editDeckCardComponent/layout.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.load();


        this.getStyleClass().add("section-line");
        this.setAlignment(Pos.CENTER);
        this.refresh();
    }

    public void onCardEdit(CardComponent.CardComponentEventHandler<Integer> handler) {
        this.onEditClickHandler = handler;
    }

    public void onCardDelete(CardComponent.CardComponentEventHandler<Integer> handler) {
        this.onDeleteClickHandler = handler;
    }

    @Override
    protected void refresh() {
        this.card.accept(this);
    }

    @FXML
    private void onCardEdit() {
        if (this.onEditClickHandler != null) {
            this.onEditClickHandler.handle(this.card.getId());
        }
    }

    @FXML
    private void onCardDelete() {
        if (this.onDeleteClickHandler != null) {
            this.onDeleteClickHandler.handle(this.card.getId());
        }
    }

    @Override
    public void visit(MCQCard mcqCard) {
        addPlainTextComponent(mcqCard.getQuestion(), "card-question");
        addMultipleAnswersEditCard(mcqCard.getAnswers());
    }

    @Override
    public void visit(BlanksCard blanksCard) {
        addPlainTextComponent(blanksCard.getQuestion(), "card-question");
        addMultipleAnswersEditCard(blanksCard.getAnswers());
    }

    @Override
    public void visit(OpenAnswerCard openAnswerCard) {
        String question = openAnswerCard.getQuestion();
        String answer = openAnswerCard.getAnswer();
        addTextComponent(question, "card-question", openAnswerCard.getQuestionType());
        addTextComponent(answer, "card-answer", openAnswerCard.getAnswerType());
    }

    private void addMultipleAnswersEditCard(List<String> answers) {
        TilePane answersPane = new TilePane();
        answersPane.setPrefColumns(4);

        for (String answer : answers) {
            Text answerName = new Text();
            answerName.getStyleClass().add("card-answer");
            answerName.setText(String.format("- %s", answer));
            answerName.wrappingWidthProperty().bind(cardDetailsBox.widthProperty());
            answersPane.getChildren().add(answerName);
        }
        cardDetailsBox.getChildren().addAll(answersPane);
        VBox.setVgrow(answersPane, Priority.ALWAYS);
    }

}

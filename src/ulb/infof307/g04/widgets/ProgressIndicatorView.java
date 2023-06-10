package ulb.infof307.g04.widgets;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ProgressIndicatorView {
    private final Stage progressIndicatorStage;
    private final ParallelTransition parallelTransition;
    private final FadeTransition showTransition;
    private final ParallelTransition hideTransition;
    private final RotateTransition rotateOutTransition;
    private final Group qSymbol;
    private final Text messageText;

    public ProgressIndicatorView() {
        this.qSymbol = createQSymbol();
        this.messageText = createMessageText();
        this.progressIndicatorStage = createProgressIndicatorStage();
        this.parallelTransition = createParallelTransition();
        this.showTransition = createShowTransition();
        this.rotateOutTransition = createRotateOutTransition();
        this.hideTransition = createHideTransition();
    }

    private Text createMessageText() {
        Text text = new Text("");
        text.setFill(Color.WHITE);
        text.setTranslateY(85);
        text.setFont(javafx.scene.text.Font.font(14));
        return text;
    }

    private Group createQSymbol() {
        Arc arc = new Arc(50, 50, 50, 50, 0, 360);
        arc.setType(ArcType.OPEN);
        arc.setStroke(Color.WHITE);
        arc.setStrokeWidth(10);
        arc.setFill(Color.TRANSPARENT);

        Line line = new Line(75, 80, 100, 95);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(10);

        return new Group(arc, line);
    }

    private Stage createProgressIndicatorStage() {
        StackPane root = new StackPane();
        root.getChildren().addAll(qSymbol, messageText);

        root.setStyle("-fx-background-color: rgba(0.1, 0.1, 0.2, 0.95); -fx-background-radius: 20;");
        root.setPrefSize(200, 200);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        return stage;
    }

    private ParallelTransition createParallelTransition() {
        RotateTransition rotateTransition = createRotateTransition();
        FadeTransition blinkTransition = createBlinkTransition();
        return new ParallelTransition(rotateTransition, blinkTransition);
    }

    private RotateTransition createRotateTransition() {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), qSymbol);
        rotateTransition.setByAngle(1800);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.EASE_BOTH);

        return rotateTransition;
    }

    private FadeTransition createBlinkTransition() {
        FadeTransition blinkTransition = new FadeTransition(Duration.seconds(0.5), qSymbol);
        blinkTransition.setFromValue(1.0);
        blinkTransition.setToValue(0.7);
        blinkTransition.setCycleCount(Timeline.INDEFINITE);
        blinkTransition.setAutoReverse(true);

        return blinkTransition;
    }

    private FadeTransition createShowTransition() {
        FadeTransition showTransition = new FadeTransition(Duration.seconds(0.25), qSymbol);
        showTransition.setFromValue(0.0);
        showTransition.setToValue(1.0);
        showTransition.setOnFinished(e -> parallelTransition.play());

        return showTransition;
    }

    private RotateTransition createRotateOutTransition() {
        RotateTransition rotateOutTransition = new RotateTransition(Duration.seconds(0.25), qSymbol);
        rotateOutTransition.setCycleCount(1);
        rotateOutTransition.setInterpolator(Interpolator.EASE_IN);

        return rotateOutTransition;
    }

    private FadeTransition createFadeOutTransition() {
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(0.5), progressIndicatorStage.getScene().getRoot());
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(e -> {
            progressIndicatorStage.hide();
        });

        return fadeOutTransition;
    }

    private ParallelTransition createHideTransition() {
        return new ParallelTransition(createFadeOutTransition(), rotateOutTransition);
    }

    public void show(String message) {
        // make sure opacity is 1.0 and rotation is 0 degrees
        messageText.setText(message);
        qSymbol.setRotate(0);
        progressIndicatorStage.getScene().getRoot().setOpacity(1.0);
        progressIndicatorStage.show();
        showTransition.play();
    }

    public void hide() {
        parallelTransition.stop();
        qSymbol.setRotate(qSymbol.getRotate() % 360);
        rotateOutTransition.setByAngle(360 - qSymbol.getRotate());
        hideTransition.play();
    }
}

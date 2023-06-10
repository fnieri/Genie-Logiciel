package ulb.infof307.g04.widgets.components;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ulb.infof307.g04.enums.CardType;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.parsers.LatexParser;

public abstract class CardComponent extends VBox {
    @FunctionalInterface
    public interface CardComponentEventHandler<T> {
        void handle(T event);
    }

    @FunctionalInterface
    public interface CardComponentEventHandlerWithReturn<T, K> {
        K handle(T event);
    }

    @FXML
    protected VBox cardDetailsBox;
    protected ICard card;

    CardComponent(ICard card) {
        this.card = card;
    }

    public void setCard(ICard card) {
        this.card = card;
        this.refresh();
    }

    protected abstract void refresh();

    Node addTextComponent(String value, String cssClass, CardType cardType) {
        if (cardType.isType(CardType.LATEX))
            return addLatexComponent(value);
        else if (cardType.isType(CardType.HTML))
            return addHTMLTextCard(value);
        else
            return addPlainTextComponent(value, cssClass);
    }

    Node addPlainTextComponent(String value, String cssClass) {
        // NOTE: Wrapping Text element, how to resize based on the parent box
        // see https://stackoverflow.com/a/74858287
        Text text = new Text();
        text.getStyleClass().add(cssClass);
        text.setText(value);
        text.wrappingWidthProperty().bind(cardDetailsBox.widthProperty());
        cardDetailsBox.getChildren().addAll(text);
        VBox.setVgrow(text, Priority.ALWAYS);
        return text;
    }

    Node addLatexComponent(String value) {
        LatexParser latexParser = new LatexParser();
        Image image = latexParser.getFormulaImage(value);
        ImageView imageView = new ImageView(image);
        cardDetailsBox.getChildren().addAll(imageView);
        VBox.setVgrow(cardDetailsBox, Priority.ALWAYS);
        return imageView;
    }

    Node addHTMLTextCard(String value) {
        Document doc = Jsoup.parse(value);
        WebView webView = new WebView();
        WebEngine webViewEngine = webView.getEngine();
        webViewEngine.loadContent(doc.html());
        webViewEngine.setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("/ulb/infof307/g04/ui/webView.css")).toExternalForm());
        webView.setMinHeight(webView.prefHeight(-1));

        VBox webViewContainer = new VBox(webView);
        webViewContainer.getStyleClass().add("section-content-box");
        webView.prefWidthProperty().bind(cardDetailsBox.widthProperty());
        webViewContainer.prefWidthProperty().bind(webView.widthProperty());
        cardDetailsBox.getChildren().add(webViewContainer);
        return webViewContainer;
    }

}

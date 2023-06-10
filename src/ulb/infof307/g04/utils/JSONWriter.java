package ulb.infof307.g04.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class JSONWriter {
    public static final String ID = "id";
    public static final String name = "name";
    public static final String tags = "tags";
    public static final String users = "users";
    public static final String cards = "cards";
    public static final String question = "question";
    public static final String questionType = "question_type";
    public static final String answer = "answer";
    public static final String answerType = "answer_type";
    public static final String type = "question_type";
    public static final String deck = "deck";
    /**
     * Returns a JSONObject representing the deck
     * @param deck the deck to be converted
     * @return a JSONObject representing the deck
     */
    public JSONObject getDeckAsJSONObject(Deck deck) {
        JSONObject jsonDeck = new JSONObject();
        JSONArray jsonCards = new JSONArray();
        for (ICard card : deck.getCards()) {
            JSONObject jsonCard = new JSONObject();
            jsonCard.put(question, card.getQuestion());
            jsonCard.put(answer, card.getAnswer());
            jsonCard.put(questionType, card.getQuestionType().getType());
            jsonCard.put(answerType, card.getAnswerType().getType());
            jsonCards.put(jsonCard);
        }
        jsonDeck.put(name, deck.getName());
        jsonDeck.put(tags, deck.getTags());
        jsonDeck.put(cards, jsonCards);

        return jsonDeck;
    }

    /**
     * Writes a JSONObject to a file at the given path
     * @param jsonDeck the JSONObject to be written
     * @param path the path to the file
     * @throws IOException if the file cannot be written
     */
    public void writeJsonFile(JSONObject jsonDeck, File path) throws IOException {

        PrintWriter out = new PrintWriter(new FileWriter(path));
        out.write(jsonDeck.toString());
        out.close();
    }

    /**
     * Reads a JSONObject from a file at the given path
     * @param path the path to the file
     * @return the JSONObject read from the file
     * @throws IOException if the file cannot be read
     */
    public JSONObject readJsonFile(File path) throws IOException {
        String content = new String(Files.readAllBytes(path.toPath()));
        return new JSONObject(content);
    }

}

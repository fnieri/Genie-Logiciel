package ulb.infof307.g04.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import ulb.infof307.g04.services.models.Card;
import ulb.infof307.g04.services.models.Deck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSONServiceTest {

    private final String name = "Name";
    private final String tag = "tag";
    private final List<String> tags = new ArrayList<>(List.of(new String[]{tag}));
    private final String testNameFile = "TestJsonExport.json";
    private final File pathTest = new File(System.getProperty("user.dir") + File.separator + "test" + File.separator + "ulb" + File.separator + "infof307" + File.separator + "g04" + File.separator + testNameFile);

    private final JSONService jsonService = new JSONService();

    private Deck createTestDeck() {
        List<Card> cards = new ArrayList<>();
        List<String> users = new ArrayList<>();
        users.add("user");
        for (int i = 0; i < 4; i++) {
            Card card = new Card(i, 0, String.valueOf(i), String.valueOf(-i));
            cards.add(card);
        }
        return new Deck(0, name, tags, users, cards);
    }

    @Test
    void getDeckAsJSONObject() {
        JSONObject testJSONDeck = jsonService.getDeckAsJSONObject(createTestDeck());

        Assertions.assertEquals(testJSONDeck.getString(jsonService.NAME), name);
        JSONArray cardsArray = testJSONDeck.getJSONArray(jsonService.CARDS);
        for (int i = 0; i < cardsArray.length(); i++) {
            Assertions.assertEquals(cardsArray.getJSONObject(i).getInt(jsonService.QUESTION), i);
            Assertions.assertEquals(cardsArray.getJSONObject(i).getInt(jsonService.ANSWER), -i);
        }
        JSONArray tagsArray = testJSONDeck.getJSONArray(jsonService.TAGS);
        for (int i = 0; i < tagsArray.length(); i++) {
            Assertions.assertEquals(tagsArray.getString(i), tags.get(i));
        }


    }

    @Test
    void writeJsonFile() throws IOException {
        JSONObject testJSONDeck = jsonService.getDeckAsJSONObject(createTestDeck());
        jsonService.writeJsonFile(testJSONDeck, pathTest);
        Assertions.assertTrue(pathTest.exists());
        Assertions.assertTrue(pathTest.isFile());
        Assertions.assertFalse(pathTest.isDirectory());
    }

    @Test
    void readJsonFile() throws IOException {
        JSONObject testJSONDeck = jsonService.getDeckAsJSONObject(createTestDeck());
        Assertions.assertEquals(jsonService.readJsonFile(pathTest).toString(), testJSONDeck.toString());
        Assertions.assertTrue(pathTest.delete());
    }
}
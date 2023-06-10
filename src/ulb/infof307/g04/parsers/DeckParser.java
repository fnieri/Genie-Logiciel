package ulb.infof307.g04.parsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckParser {

    public Deck parse(String json) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode deckJson = objectMapper.readTree(json);

        int id = deckJson.get("id").asInt();
        String name = deckJson.get("name").asText();
        List<String> tags = parseTags(deckJson.get("tags"));
        List<ICard> cards = parseCards(deckJson.get("cards"));
        List<String> users = parseUsers(deckJson.get("users"));

        return new Deck(id, name, tags, users, cards);
    }

    public List<Deck> parse(ArrayNode deckJson) throws JsonProcessingException {
        List<Deck> decks = new ArrayList<>();
        for (JsonNode deck : deckJson) {
            decks.add(
                    parse(deck.toString())
            );
        }
        return decks;
    }

    private List<String> parseTags(JsonNode tagsJson) {
        List<String> tags = new ArrayList<>();
        for (final JsonNode tag : tagsJson) {
            tags.add(tag.asText());
        }
        return tags;
    }

    /**
     * Parses the users from the json tree
     * @param usersJson the json tree containing the users
     */
    private List<String> parseUsers(JsonNode usersJson) {
        List<String> users = new ArrayList<>();
        for (final JsonNode user : usersJson) {
            users.add(user.asText());
        }
        return users;
    }

    /**
     * Parses the cards from the json tree
     * @param cardsJson the json tree containing the cards
     * @throws JsonProcessingException if the json tree is invalid
     */
    private List<ICard> parseCards(JsonNode cardsJson) throws JsonProcessingException {
        List<ICard> cards = new ArrayList<>();
        CardParser cardParser = new CardParser();
        for (final JsonNode cardJson : cardsJson) {
            ICard card = cardParser.parse(cardJson.toString());
            cards.add(card);
        }
        return cards;
    }
}

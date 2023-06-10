package ulb.infof307.g04.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ulb.infof307.g04.interfaces.models.ICard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an instance of a card deck.
 * It is used to store general deck information, as well as
 * the cards it contains.
 *
 * @see ICard
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deck {
    private int id;
    private String name;
    private List<String> tags;
    private List<ICard> cards;
    private List<String> users;

    /**
     * Instantiation constructor for class Deck
     *
     * @param name  title of the deck, given by the author
     * @param tags  tags of the deck, given by the author
     */
    public Deck(int id, String name, List<String> tags, List<String> users, List<ICard> cards) {
        this.id = id;
        this.name = name;
        this.users = users;
        this.tags = tags;
        this.cards = new ArrayList<>(cards);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", cards=" + cards +
                '}';
    }

    public int size() {
        return cards.size();
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public List<ICard> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public String getTagsAsString() {
        return String.join(", ", tags);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
    public List<String> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public String getTheme() {
        return tags.get(0);
    }
}

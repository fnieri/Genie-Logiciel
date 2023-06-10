package ulb.infof307.g04.interfaces.view;

import java.util.List;

import ulb.infof307.g04.models.Deck;

public interface IProfileViewController extends IViewController {
    void setDecks(List<Deck> decks);
    void setTags(List<String> tags);
}

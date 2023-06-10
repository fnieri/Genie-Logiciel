package ulb.infof307.g04.interfaces.view;

import ulb.infof307.g04.models.StoreDeck;
import java.util.List;

public interface IStoreViewController extends IViewController{
    void setDeckBatch(List<StoreDeck> storeDecks);
    void showDeckList(List<StoreDeck> storeDecks);
}
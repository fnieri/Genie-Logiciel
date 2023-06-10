package ulb.infof307.g04.interfaces.view;

import ulb.infof307.g04.interfaces.models.ICard;

import java.util.List;


public interface IEditDeckViewController extends IViewController {
    void setDeckCategories(List<String> categories);
    void setDeckTitle(String title);
    void setDeckCards(List<ICard> cards);
    void setupCardTypes(List<String> types);
}

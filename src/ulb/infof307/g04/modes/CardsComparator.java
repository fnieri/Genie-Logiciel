package ulb.infof307.g04.modes;

import java.util.Comparator;

import ulb.infof307.g04.interfaces.models.ICard;
/**
 * @author @fnieri
 * Class that compares two cards mastery status for priority queue insertion
 */
public class CardsComparator implements Comparator<ICard> {

    @Override
    public int compare(ICard o1, ICard o2) {
        return Integer.compare(o1.getMasteryLevel().score(), o2.getMasteryLevel().score());
    }
}

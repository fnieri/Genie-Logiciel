package ulb.infof307.g04.controllers;

import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.factory.StoreDeckServiceFactory;
import ulb.infof307.g04.interfaces.controller.IStoreController;
import ulb.infof307.g04.interfaces.services.IStoreDeckService;
import ulb.infof307.g04.interfaces.view.IStoreViewController;
import ulb.infof307.g04.models.StoreDeck;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import javafx.scene.control.Alert.AlertType;

/**
 * Controller for the Store view (declared in store.fmxl).
 * <p>
 * The Store view contains all shared decks from users.
 *
 * @see ulb.infof307.g04.view.StoreViewController
 */
public class StoreController extends AbstractController implements IStoreController {
    private final IStoreDeckService service;
    private final IStoreViewController view;
    private List<StoreDeck> mostPopularStoreDecks = new ArrayList<>();
    private List<StoreDeck> allStoreDecks = new ArrayList<>();
    private static final int limitPerPage = 4;

    private int maxIndex;
    private int currentIndexPage = 0;

    /**
     * Constructor of the StoreContoller class.
     *
     * @param view the view controller that the controller interacts with.
     */
    public StoreController(IStoreViewController view) {
        super(view);
        service = StoreDeckServiceFactory.getInstance();
        this.view = view;
    }

    @Override
    public void initialize() {
        super.initialize();
        runAsync(() -> {
            try {
                return service.getStoreDecks();
            } catch (ApiException e) {
                throw new CompletionException(e);
            }
        }, storeDecks -> {
            mostPopularStoreDecks = storeDecks.get(0);
            allStoreDecks = storeDecks.get(1);
            maxIndex = (mostPopularStoreDecks.size() / limitPerPage);
            this.initCarousel();
            view.showDeckList(this.allStoreDecks);
        }, "Loading store decks", "Can't get store decks from server.");
    }

    @Override
    public void initCarousel() {
        if (maxIndex == 0) {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(0, mostPopularStoreDecks.size()));
        } else {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(0, limitPerPage));
        }
    }

    @Override
    public void setNextPage() {
        currentIndexPage = Math.floorMod(currentIndexPage + 1, maxIndex + 1);
        int currentListIndex = currentIndexPage * limitPerPage;
        if (currentIndexPage == maxIndex) {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(currentListIndex, mostPopularStoreDecks.size()));
        } else {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(currentListIndex, currentListIndex + limitPerPage));
        }
    }

    @Override
    public void setPreviousPage() {
        currentIndexPage = Math.floorMod(currentIndexPage - 1, maxIndex + 1);
        int currentListIndex = currentIndexPage * limitPerPage;
        if (currentIndexPage == maxIndex) {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(currentListIndex, mostPopularStoreDecks.size()));
        } else {
            view.setDeckBatch(this.mostPopularStoreDecks.subList(currentListIndex, currentListIndex + limitPerPage));
        }
    }

    @Override
    public void downloadDeck(int deckId) {

        runAsync(() -> {
                    try {
                        service.downloadDeck(deckId);
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, () -> {
                    view.showAlert(AlertType.INFORMATION, "Deck downloaded", "This deck is now in your decks collection", null);
                },
                "Downloading deck", "Failed to download deck"
        );
    }

    @Override
    public void catalogSearch(String query) {
        List<StoreDeck> shownDecks = new ArrayList<>();
        for (StoreDeck deck : this.allStoreDecks) {
            if (deck.getName().contains(query)) {
                shownDecks.add(deck);
            }
        }
        view.showDeckList(shownDecks);
    }
}
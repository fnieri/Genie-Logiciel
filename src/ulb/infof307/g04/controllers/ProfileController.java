package ulb.infof307.g04.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletionException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import ulb.infof307.g04.AppState;
import ulb.infof307.g04.enums.EPages;
import ulb.infof307.g04.exceptions.api.ApiException;
import ulb.infof307.g04.exceptions.deck.DeckIsEmpty;
import ulb.infof307.g04.factory.DeckServiceFactory;
import ulb.infof307.g04.factory.StoreDeckServiceFactory;
import ulb.infof307.g04.interfaces.controller.IProfileController;
import ulb.infof307.g04.interfaces.services.IDeckService;
import ulb.infof307.g04.interfaces.services.IStoreDeckService;
import ulb.infof307.g04.interfaces.view.IProfileViewController;
import ulb.infof307.g04.models.Deck;
import ulb.infof307.g04.models.Tag;
import ulb.infof307.g04.modes.FreeMode;
import ulb.infof307.g04.modes.LearningMode;
import ulb.infof307.g04.modes.ReviewMode;
import ulb.infof307.g04.modes.StudyMode;
import ulb.infof307.g04.utils.JSONWriter;
import ulb.infof307.g04.utils.PDFWriter;


/**
 * Controller for sub-view Profile of the Home view (declared in profile.fmxl).
 * <p>
 * The Profile subview contains user-related widgets (e.g. his decks list).
 *
 * @see ulb.infof307.g04.view.ProfileViewController
 * @see ulb.infof307.g04.interfaces.controller.IProfileController
 */
public class ProfileController extends AbstractController implements IProfileController {
    private final IDeckService deckService = DeckServiceFactory.getInstance();
    private final IStoreDeckService storeDeckService = StoreDeckServiceFactory.getInstance();
    private final JSONWriter jsonWriter;
    private final AppState appState;
    private final IProfileViewController view;

    /**
     * Constructor for the ProfileController
     *
     * @param view the view that the controller will control
     */
    public ProfileController(IProfileViewController view) {
        super(view);
        jsonWriter = new JSONWriter();
        appState = AppState.getInstance();
        this.view = view;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.refreshDecks();
    }

    /**
     * Refreshes the decks list and their tags.
     */
    private void refreshDecks() {
        runAsync(
                () -> {
                    try {
                        return this.deckService.getDecks();
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                decks -> {
                    HashSet<String> tags = new HashSet<>();
                    for (Deck deck : decks) {
                        tags.addAll(deck.getTags());
                    }
                    view.setDecks(decks);
                    view.setTags(new ArrayList<>(tags));
                },
                "Fetching decks",
                "Error while fetching decks"
        );
    }

    @Override
    public void createDeck(String name, String tags) {
        runAsync(
                () -> {
                    try {
                        return deckService.createDeck(name, new ArrayList<>(Tag.getTagsFromCommaSeparatedString(tags)));
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                },
                deck -> {
                    this.refreshDecks();
                },
                "Creating deck",
                "Error while creating deck"
        );
    }

    @Override
    public void deleteDeck(int deckId) {
        try {
            deckService.deleteDeck(deckId);
            this.refreshDecks();
        } catch (ApiException e) {
            showAlertForException(e, "Deck Deletion Error");
        }
    }

    @Override
    public void filterDecks(ObservableList<String> tags) {
        runAsync(
                () -> {
                    try {
                        return deckService.getDecks();
                    } catch (ApiException e) {
                        throw new CompletionException(e);
                    }
                }, decks -> {
                    List<Deck> filteredDecks = new ArrayList<>();
                    for (Deck deck : decks) {
                        for (String tag : deck.getTags()) {
                            if (tags.contains(tag)) {
                                filteredDecks.add(deck);
                                break;
                            }
                        }
                    }
                    view.setDecks(filteredDecks);
                }, "Filtering decks", "Error while filtering decks"
        );
    }

    @Override
    public void onDeckReview(int deckID) {
        try {
            Deck deck = deckService.getDeck(deckID);
            ReviewMode reviewMode= new ReviewMode(deck);
            onDeckLearn(reviewMode);
        }
        catch (ApiException e) {
            showAlertForException(e, "Deck error");
        } catch (DeckIsEmpty e) {
            view.showAlert( AlertType.ERROR,"Learning error", "The deck you want to study is empty", "");

        }
    }

    @Override
    public void onDeckEdit(int deckID) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deckID", deckID);
        view.switchView(EPages.EDIT_DECK, params);
    }

    @Override
    public void onFreeMode(int deckID) {
        try {
            Deck deck = deckService.getDeck(deckID);
            FreeMode freeMode = new FreeMode(deck);
            onDeckLearn(freeMode);
        } catch (ApiException e) {
            showAlertForException(e, "Deck error");
        } catch (DeckIsEmpty e) {
            view.showAlert( AlertType.ERROR,"Learning error", "The deck you want to study is empty", "");
        }
    }

    @Override
    public void onDeckStudy(int deckID) {
        try {
            Deck deck = deckService.getDeck(deckID);
            StudyMode studyMode = new StudyMode(deck);
            onDeckLearn(studyMode);
        }
        catch (ApiException e) {
            showAlertForException(e, "Deck error");
        } catch (DeckIsEmpty e) {
            view.showAlert( AlertType.ERROR,"Learning error", "The deck you want to study is empty", "");
        }
    }

    /**
     * Switches to the chosen learning mode.
     *
     * @param learningMode - the mode to switch to
     */
    private void onDeckLearn(LearningMode learningMode) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("learning_mode", learningMode);
        view.switchView(EPages.LEARNING_MODE, params);
    }

    @Override
    public void downloadDeck(int deckId) {
        try {
            Deck deck = deckService.getDeck(deckId);
            File selectedPath = view.showSaveFileDialog("Browse Files", String.format("%s.pdf", deck.getName()));
            Runnable task = () -> {
                try {
                    PDFWriter pdfWriter = new PDFWriter();
                    PDDocument document = new PDDocument();
                    pdfWriter.exportDeckToPDF(deck, document);
                    document.save(selectedPath.toString());
                    document.close();
                } catch (IOException e) {
                    showAlertForException(e, "An error occured");
                }};

                // Create a new thread and run the task on it
                Thread thread = new Thread(task);
                thread.start();
        } catch (ApiException e) {
            showAlertForException(e, "API error");
        }
    }

    @Override
    public JSONObject exportDeck(int deckID) {
        try {
            return deckService.exportDeck(deckID);
        } catch (ApiException e) {
            showAlertForException(e, "Failed to export deck");
        }
        return null;
    }

    @Override
    public void importDeck(JSONObject jsonDeck) {
        try {
            deckService.importDeck(jsonDeck, appState.getUserId());
            this.refreshDecks();
        } catch (ApiException e) {
            showAlertForException(e, "null");
        }
    }

    @Override
    public void writeJSONFile(JSONObject jsonDeck, File path) {
        try {
            jsonWriter.writeJsonFile(jsonDeck, path);
        } catch (IOException e) {
            showAlertForException(e, "Failed to write .json file");
        }
    }

    @Override
    public JSONObject readJSONFile(File path) {
        try {
            return jsonWriter.readJsonFile(path);
        } catch (IOException e) {
            showAlertForException(e, "Failed to read .json file");
        }
        return null;
    }

    @Override
    public Deck getDeckByID(int deckId) {
        try {
            return deckService.getDeck(deckId);
        } catch (ApiException e) {
            showAlertForException(e, "Failed to fetch deck");
        }
        return null;
    }

    @Override
    public void onDeckShare(int deckId) {
        try {
            storeDeckService.shareDeck(deckId);
            view.showAlert(AlertType.INFORMATION, "Deck shared", "Your deck has been shared on the store.", null);
        } catch (ApiException e) {
            showAlertForException(e, "Failed to shared deck");
        }
    }
}
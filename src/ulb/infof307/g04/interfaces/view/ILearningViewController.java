package ulb.infof307.g04.interfaces.view;

import ulb.infof307.g04.interfaces.models.ICard;

public interface ILearningViewController extends IViewController {
    void setDeckName(String name);
    void setPerformanceTypeButtonVisible(boolean isVisible);
    void setCard(ICard card);
    void setScore(int score);
    void setProgress(int current, int max);
    void setNavButtonsVisibility(boolean isPrevBtnVisible, boolean isNextButtonVisible);
    void setNavButtonsDisable(boolean isPrevBtnDisabled, boolean isNextButtonDisabled);
    void setScoreVisibility(boolean isVisible);
    void setFlipBtnDisable(boolean b);
}

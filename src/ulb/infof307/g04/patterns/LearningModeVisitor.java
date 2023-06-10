package ulb.infof307.g04.patterns;

import ulb.infof307.g04.modes.FreeMode;
import ulb.infof307.g04.modes.ReviewMode;
import ulb.infof307.g04.modes.StudyMode;

/**
 * Interface used by the LearningController to implement
 * the Visitor pattern.
 * <p>
 * Each visit method is overridden to perform specific actions 
 * based on the learning mode that the app is currently in.
 * 
 * @see ulb.infof307.g04.controllers.LearningController
 */
public interface LearningModeVisitor {
    void visit(StudyMode learningMode);
    void visit(FreeMode freeMode);
    void visit(ReviewMode reviewMode);
}
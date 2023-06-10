package ulb.infof307.g04.patterns;

import ulb.infof307.g04.models.BlanksCard;
import ulb.infof307.g04.models.MCQCard;
import ulb.infof307.g04.models.OpenAnswerCard;

public interface CardVisitor {
    void visit(MCQCard mcqCard);
    void visit(OpenAnswerCard openAnswerCard);
    void visit(BlanksCard blanksCard);
}

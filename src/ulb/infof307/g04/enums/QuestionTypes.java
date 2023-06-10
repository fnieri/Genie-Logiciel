package ulb.infof307.g04.enums;

/**
 * Lists the different question types that can be used 
 * for a card
 */
public enum QuestionTypes {
    MCQ,
    CBT,
    OAR;

    /** 
     * Converts an enum value to a string
     */
    public String toViewString() {
        return switch (this) {
            case MCQ -> "Multiple choice";
            case CBT -> "Checkbox";
            case OAR -> "Open answer";
        };
    }

    /**
     * Converts a string value to its corresponding enum value 
     */
    public static QuestionTypes fromViewString(String viewString) {
        return switch (viewString) {
            case "Multiple choice" -> MCQ;
            case "Checkbox" -> CBT;
            case "Open answer" -> OAR;
            default -> MCQ;
        };
    }

    public int getValue() {
        return switch (this) {
            case MCQ -> 0;
            case CBT -> 1;
            case OAR -> 2;
        };
    }
}
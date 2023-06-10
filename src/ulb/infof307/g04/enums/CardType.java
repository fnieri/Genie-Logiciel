package ulb.infof307.g04.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CardType {
    PLAIN_TEXT,
    HTML,
    LATEX,
    MCQ,
    BLNK,
    OAR;

    /**
     * @note This method is used by jackson to serialize the enum
     * @return The string representation of the enum
     */
    @JsonValue
    public String getType() {
        return switch (this) {
            case PLAIN_TEXT -> "text";
            case HTML -> "html";
            case LATEX -> "ltx";
            case MCQ -> "mcq";
            case BLNK -> "blnk";
            case OAR -> "oar";
        };
    }

    /**
     * @note This method is used by jackson to deserialize the enum
     * @param type The string representation of the enum
     * @return The enum
     */
    @JsonCreator
    public static CardType fromString(String type) {
        if (type == null || type.isEmpty()) {
            return PLAIN_TEXT; // Assign a default value for empty strings
        }

        return switch (type) {
            case "html" -> HTML;
            case "ltx" -> LATEX;
            case "mcq" -> MCQ;
            case "blnk" -> BLNK;
            case "oar" -> OAR;
            default -> PLAIN_TEXT;
        };
    }

    /**
     * @note This method is used to display the enum in the view
     * @return The string representation of the enum
     */
    public String toViewString() {
        return switch (this) {
            case PLAIN_TEXT -> "Plain text";
            case HTML -> "HTML";
            case LATEX -> "LaTeX";
            case MCQ -> "Multiple Choices";
            case BLNK -> "Cloze-based Text";
            case OAR -> "Open Reponse";
        };
    }

    /**
     * @note This method is used to deserialize the enum from the view
     * @param viewString The string representation of the enum
     * @return The enum
     */
    public static CardType fromViewString(String viewString) {
        return switch (viewString) {
            case "HTML" -> HTML;
            case "LaTeX" -> LATEX;
            case "Multiple Choices" -> MCQ;
            case "Cloze-based Text" -> BLNK;
            case "Open Reponse" -> OAR;
            default -> PLAIN_TEXT;
        };
    }

    public boolean isType(CardType type) {
        return this == type;
    }

}
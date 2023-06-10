package ulb.infof307.g04.enums;

/**
 * This enum represents the different levels of mastery a user can have on a card
 */
public enum CardMasteryLevels {
    VERY_BAD,
    BAD,
    NOT_YET_LEARNED,
    MODERATE,
    GOOD,
    VERY_GOOD;


    /**
     * This method returns the CardMasteryLevels enum corresponding to the given score
     * @param score the score to convert
     * @return the CardMasteryLevels enum corresponding to the given score
     */
    public static CardMasteryLevels fromInt(int score ) {
        return switch(score) {
            case 3 -> CardMasteryLevels.VERY_GOOD;
            case 2 -> CardMasteryLevels.GOOD;
            case 1 -> CardMasteryLevels.MODERATE;
            case -1 -> CardMasteryLevels.BAD;
            case -2 -> CardMasteryLevels.VERY_BAD;
            default -> CardMasteryLevels.NOT_YET_LEARNED;
        };
    }

    public boolean lessThanOrEqualTo(CardMasteryLevels other) {
        return this.score() <= other.score();
    }

    public boolean greaterThanOrEqualTo(CardMasteryLevels other) {
        return this.score() >= other.score();
    }


    public int score() {
        return switch (this) {
            case VERY_GOOD -> 3;
            case GOOD -> 2;
            case MODERATE -> 1;
            case BAD -> -1;
            case VERY_BAD -> -2;
            case NOT_YET_LEARNED -> 0;
        };
    }


}


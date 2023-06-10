package ulb.infof307.g04.parsers;

import java.util.List;

public class CardContentParser {
    public List<String> parseAnswers(String answers) {
        String answerSeparator = ";";
        return List.of(answers.split(answerSeparator));
    }
}

package ulb.infof307.g04.utils;

import java.util.Locale;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class TextToSpeech {
    private final Synthesizer synthesizer;

    public TextToSpeech() throws EngineException, AudioException {

        // Set property as Kevin Dictionary
        System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        // Register Engine
        Central.registerEngineCentral(
                "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

        // Create a Synthesizer
        synthesizer = Central.createSynthesizer(
                new SynthesizerModeDesc(Locale.US));

        // Allocate synthesizer
        synthesizer.allocate();

        // Resume Synthesizer
        synthesizer.resume();

    }

    public void speak(String text) throws InterruptedException {

        synthesizer.speakPlainText(text, null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

    }
}

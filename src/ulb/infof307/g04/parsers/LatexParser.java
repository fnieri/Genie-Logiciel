package ulb.infof307.g04.parsers;
import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import java.awt.*;


public class LatexParser {

    /**
     * Parse a LaTeX equation
     * @param equation the equation to parse
     * @return the parsed equation
     * @throws ParseException if the equation is invalid
     */
    public TeXFormula parseEquation(String equation) throws ParseException {
        return new TeXFormula(equation);
    }

    public javafx.scene.image.Image getFormulaImage(String equation) throws ParseException {
        TeXFormula formula = parseEquation(equation);
        Image bufferedImage = formula.createBufferedImage(TeXFormula.SERIF, 20, Color.WHITE, null);
        return SwingFXUtils.toFXImage((BufferedImage) bufferedImage, null);
    }
}

package ulb.infof307.g04.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import ulb.infof307.g04.interfaces.models.ICard;
import ulb.infof307.g04.models.Deck;

import java.awt.geom.AffineTransform;
import java.io.IOException;

public class PDFWriter {

    PDFont pdfFontBold = PDType1Font.HELVETICA_BOLD;
    PDFont pdfFont = PDType1Font.HELVETICA;

    public PDFWriter() {
    }

    public void exportDeckToPDF(Deck deck, PDDocument document) throws IOException {
        writeTitleAndTheme(deck, pdfFontBold, pdfFont, document);
        writeCards(deck, pdfFontBold, pdfFont, document);
    }



    public void writeTitleAndTheme(Deck deck, PDFont pdfFont, PDFont pdfFont2, PDDocument document) throws IOException {
        int fontSize = 20;
        int marginTop = 10;
        float titleWidth = pdfFont.getStringWidth(deck.getName()) / 1000 * fontSize;
        float titleHeight = pdfFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        PDPage mainPage = new PDPage(PDRectangle.A6);
        PDRectangle mediaBox = mainPage.getMediaBox();
        // Inverted coordinates for landscape display
        float startX = (mediaBox.getHeight() - titleHeight) / 2 - titleWidth;
        float startY = -(mediaBox.getWidth() - marginTop - titleWidth) / 2;

        mainPage.setRotation(90);
        document.addPage(mainPage);
        PDPageContentStream contentStream = new PDPageContentStream(document, mainPage, PDPageContentStream.AppendMode.OVERWRITE, true, true);
        contentStream.transform(new Matrix(AffineTransform.getRotateInstance(Math.toRadians(90.0), 0.0, 0.0)));
        contentStream.setLeading(20);
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, startY);
        addTextToPDFPage(contentStream, pdfFont, fontSize, "Title: " + deck.getName());
        addTextToPDFPage(contentStream, pdfFont2, fontSize, "Theme: " + deck.getTheme());
        contentStream.endText();
        contentStream.close();
    }

    /**
     * Adds text to a PDF page
     *
     * @param stream   the content stream to write to
     * @param font     the font to use for the PDF
     * @param fontSize the size of the font
     * @param text     the string to write on the PDF
     */
    private void addTextToPDFPage(PDPageContentStream stream, PDFont font, int fontSize, String text) throws IOException {
            stream.setFont(font, fontSize);
            stream.newLine();
            stream.showText(text);
    }

    /**
     * Write the deck cards to a PDF file.
     *
     * @param deck the deck whose cards are added
     * @param pdfFont the bold font to write with
     * @param pdfFont2 the normal font to write with
     * @param document the PDF document to write to
     */
    private void writeCards(Deck deck, PDFont pdfFont, PDFont pdfFont2,  PDDocument document) throws IOException {
        int fontSize = 10;
        int index = 1;
        for (ICard card : deck.getCards()) {
            PDPageContentStream contentStream = this.initContentStream(document);
            contentStream.beginText();
            contentStream.newLineAtOffset(20, -40);
            contentStream.setLeading(17);

            addTextToPDFPage(contentStream, pdfFont, fontSize, "Question " + index + ": ");
            String[] questChars = card.getQuestion().replaceAll("\n", " ").replaceAll("\t", " ").split("");
            writeWrappedLineToPDF(contentStream, pdfFont, fontSize, questChars);

            addTextToPDFPage(contentStream, pdfFont2, fontSize, "Answer " + index + ": ");
            String[] answerChars = card.getAnswer().replaceAll("\n", " ").replaceAll("\t", " ").split("");
            writeWrappedLineToPDF(contentStream, pdfFont2, fontSize, answerChars);

            contentStream.endText();
            contentStream.close();
            index++;
        }
    }

    /**
     * Inits a page content stream in A6 size and landscape mode
     *
     * @param document the PDF document related to the content stream
     */
    private PDPageContentStream initContentStream(PDDocument document) throws IOException {
        PDPage page = new PDPage(PDRectangle.A6);
        page.setRotation(90);
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
        contentStream.transform(new Matrix(AffineTransform.getRotateInstance(Math.toRadians(90.0), 0.0, 0.0)));

        return contentStream;
    }


    /**
     * Writes a line on a PDF page while taking the page width in consideration for line breaks.
     *
     * @param contentStream the stream to write to
     * @param font the font to use
     * @param fontSize the size of the font
     * @param chars all the characters contained in the text string to write
     */
    private void writeWrappedLineToPDF(PDPageContentStream contentStream, PDFont font, int fontSize, String[] chars) throws IOException {
        int maxCharPerLine = 60;
        contentStream.setFont(font, fontSize);
        contentStream.newLine();
        int charCount = 0;
        for (String char_ : chars) {
            contentStream.showText(char_);
            if (charCount++ == maxCharPerLine) {
                contentStream.newLine();
                charCount = 0;
            }
        }
        contentStream.newLine();
    }


}

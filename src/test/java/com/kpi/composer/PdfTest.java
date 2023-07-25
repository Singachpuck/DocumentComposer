package com.kpi.composer;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;


public class PdfTest {

    @Test
    void touch() throws IOException {
        final String path = "test.pdf";
        try (final PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage();
            final PDPageContentStream contentStream = new PDPageContentStream(document, page);
            final PDFont font = HELVETICA;
            contentStream.beginText();
            contentStream.setFont(font, 18 );
//            contentStream.setLeading(18.5f);
            contentStream.newLineAtOffset(0, 700);
            final String text = "Some random place with placeholder.";
//            font.getStringWidth("fqeqe");
            contentStream.showText(text);
//            contentStream.showText(text);
            contentStream.endText();
            contentStream.close();
            document.addPage(page);
            document.save(path);
        }
    }

    @Test
    void findTextBlocks() {

    }

    private int findTextBlocks(PDPageTree pages) throws IOException {
        int textBlocks = 0;
        for (PDPage page : pages) {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List<?> tokens = parser.getTokens();
            for (int j = 0; j < tokens.size(); j++) {
                Object next = tokens.get(j);
                if (next instanceof Operator op) {
                    if (op.getName().equals("BT")) {
                        textBlocks++;
                    }
                }
            }
            // now that the tokens are updated we will replace the page content stream.
        }
        return textBlocks;
    }

    @Test
    void replaceTest() throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("test.pdf");
        try (final PDDocument document = PDDocument.load(new File(resource.toURI()))) {
            replaceText(document, "\\$\\{\\}", "repl");
            document.save("test.pdf");
        }
    }

    @Test
    void myFunc() throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("test.pdf");
        try (final PDDocument document = PDDocument.load(new File(resource.toURI()))) {
            myFuncReplace(document, "\\$\\{\\}", "repl");
            document.save("test.pdf");
        }
    }

    private void myFuncReplace(PDDocument document, String searchString, String replacement) throws IOException {
        if ("".equals(searchString) || "".equals(replacement)) {
            return;
        }
        PDPageTree pages = document.getDocumentCatalog().getPages();
        for (PDPage page : pages) {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            PDResources resources = page.getResources();
            List<?> tokens = parser.getTokens();
            boolean openTextBlock = false;
            PDFont currentFont = null;
            COSNumber currentFontSize = null;
            float currentOffsetX = 0;
            float currentOffsetY = 0;
            PDRectangle currentSize = page.getMediaBox();
            for (int i = 0; i < tokens.size(); i++) {
                Object token = tokens.get(i);
                if (token instanceof Operator operator) {
                    if (operator.getName().equals("BT")) {
                        if (openTextBlock) {
                            throw new RuntimeException("Error processing pdf");
                        }
                        openTextBlock = true;
                        currentFont = null;
                        currentFontSize = null;
                        currentOffsetX = 0;
                        currentOffsetY = 0;
                    }
                    if (operator.getName().equals("ET")) {
                        if (!openTextBlock) {
                            throw new RuntimeException("Error processing pdf");
                        }
                        openTextBlock = false;
                    }
                    if (operator.getName().equals("Tf")) {
                        currentFontSize = (COSNumber) tokens.get(i - 1);
                        COSName fontName = (COSName) tokens.get(i - 2);
                        currentFont = resources.getFont(fontName);
                    }
                    if (operator.getName().equals("Td")) {
                        COSNumber x = (COSNumber) tokens.get(i - 2);
                        COSNumber y = (COSNumber) tokens.get(i - 1);
                        currentOffsetX += x.floatValue();
                        currentOffsetY += y.floatValue();
                    }
                    if (operator.getName().equals("Tj")) {
                        COSString value = (COSString) tokens.get(i - 1);
                        if (fits(value.getString(), currentFontSize.floatValue(), currentFont, currentSize.getWidth() - currentOffsetX)) {
                            System.out.println("Good");
                        } else {
                            System.out.println("doesn't fit");
                        }
                    }
                }
            }
        }
    }

    private boolean fits(String subString, float fontSize, PDFont pdfFont, float width) throws IOException {

        float size = fontSize * pdfFont.getAverageFontWidth() * subString.length() / 1000;
        return size <= width;
    }

    private PDDocument replaceText(PDDocument document, String searchString, String replacement) throws IOException {
        if ("".equals(searchString) || "".equals(replacement)) {
            return document;
        }
        PDPageTree pages = document.getDocumentCatalog().getPages();
        for (PDPage page : pages) {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List<?> tokens = parser.getTokens();
            for (int j = 0; j < tokens.size(); j++) {
                Object next = tokens.get(j);
                if (next instanceof Operator op) {
                    //Tj and TJ are the two operators that display strings in a PDF
                    if (op.getName().equals("Tj")) {
                        // Tj takes one operator and that is the string to display so lets update that operator
                        COSString previous = (COSString) tokens.get(j - 1);
                        String string = previous.getString();
                        string = string.replaceFirst(searchString, replacement);
                        previous.setValue(string.getBytes());
                    } else if (op.getName().equals("TJ")) {
                        COSArray previous = (COSArray) tokens.get(j - 1);
                        for (int k = 0; k < previous.size(); k++) {
                            Object arrElement = previous.getObject(k);
                            if (arrElement instanceof COSString cosString) {
                                String string = cosString.getString();
                                string = string.replaceFirst(searchString, replacement);
                                cosString.setValue(string.getBytes());
                            }
                        }
                    }
                }
            }
            // now that the tokens are updated we will replace the page content stream.
            PDStream updatedStream = new PDStream(document);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
//            final Operator operator = new PDPageContentStream();
//            tokenWriter.writeToken();
            tokenWriter.writeTokens(tokens);
            page.setContents(updatedStream);
            out.close();
        }
        return document;
    }

    private void longLines() throws IOException {
        PDDocument doc = null;
        try
        {
            doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            PDFont pdfFont = PDType1Font.HELVETICA;
            float fontSize = 25;
            float leading = 1.5f * fontSize;

            PDRectangle mediabox = page.getMediaBox();
            float margin = 72;
            float width = mediabox.getWidth() - 2*margin;
            float startX = mediabox.getLowerLeftX() + margin;
            float startY = mediabox.getUpperRightY() - margin;

            String text = "I am trying to create a PDF file with a lot of text contents in the document. I am using PDFBox";
            List<String> lines = new ArrayList<>();
            int lastSpace = -1;
            while (text.length() > 0)
            {
                int spaceIndex = text.indexOf(' ', lastSpace + 1);
                if (spaceIndex < 0)
                    spaceIndex = text.length();
                String subString = text.substring(0, spaceIndex);
                float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
                System.out.printf("'%s' - %f of %f\n", subString, size, width);
                if (size > width)
                {
                    if (lastSpace < 0)
                        lastSpace = spaceIndex;
                    subString = text.substring(0, lastSpace);
                    lines.add(subString);
                    text = text.substring(lastSpace).trim();
                    System.out.printf("'%s' is line\n", subString);
                    lastSpace = -1;
                }
                else if (spaceIndex == text.length())
                {
                    lines.add(text);
                    System.out.printf("'%s' is line\n", text);
                    text = "";
                }
                else
                {
                    lastSpace = spaceIndex;
                }
            }

            contentStream.beginText();
            contentStream.setFont(pdfFont, fontSize);
            contentStream.newLineAtOffset(startX, startY);
            for (String line: lines)
            {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -leading);
            }
            contentStream.endText();
            contentStream.close();

            doc.save(new File("break-long-string.pdf"));
        }
        finally
        {
            if (doc != null)
            {
                doc.close();
            }
        }
    }
}

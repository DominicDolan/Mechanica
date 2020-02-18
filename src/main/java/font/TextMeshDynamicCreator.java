package font;

import display.Game;

import java.util.Objects;

/**
 * Created by domin on 29 Mar 2017.
 */

public class TextMeshDynamicCreator {

    private static final double LINE_HEIGHT = 0.03f;
    private static final int SPACE_ASCII = 32;
    private static final int RETURN = '\n';
    private final float[] translatedPoints;
    private final TextBufferData bufferData;

    private final Line[] savedLines = new Line[32];

    private MetaFile metaData;

    public TextMeshDynamicCreator() {
        translatedPoints = new float[12];
        bufferData = new TextBufferData();
        for (int i = 0; i < savedLines.length; i++) {
            savedLines[i] = new Line();
        }
    }

    public TextBufferData createTextMesh(GUIText text) {
        metaData = text.getMetaFile();
        int characterCount = Objects.requireNonNull(text.getCharArrayString()).length;
        int spaces = 0;
        for (char c : text.getCharArrayString()) {
            if (c == ' ') spaces++;
        }
        bufferData.set(characterCount - spaces);
        createQuadVertices(text, updateStructure(text));
        bufferData.flip();
        return bufferData;
    }

    private int updateStructure(GUIText text){
        int currentLine = 0;
        savedLines[currentLine].set(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
        savedLines[currentLine].setCurrentWord();

        for (char c: Objects.requireNonNull(text.getCharArrayString())){
            boolean returnCharacter = ((int) c == RETURN);
            if ((int) c == SPACE_ASCII || returnCharacter) {

                boolean added = false;
                if (!returnCharacter)
                    added = savedLines[currentLine].attemptToAddWord();

                if (!added){
                    currentLine++;
                    savedLines[currentLine].set(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
                    savedLines[currentLine].attemptToAddWord();
                }
                continue;
            }

            savedLines[currentLine]
                    .getCurrentWord()
                    .addCharacter(metaData.getCharacter(c));
        }
        savedLines[currentLine].attemptToAddWord();
        return currentLine+1;
    }

    private void createQuadVertices(GUIText text, int NoOfLines) {
        text.setNumberOfLines(NoOfLines);
        double cursorX = 0f;
        double cursorY = 0f;
        for (int line = 0; line < NoOfLines; line++) {
            if (text.isCentered()) {
                cursorX = (savedLines[line].getMaxLength() - savedLines[line].getLineLength())/2;
            }
            for (int word = 0; word <savedLines[line].getNumberOfWords(); word++) {
                for (Character letter : savedLines[line].getWord(word).getCharacters()) {
                    addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize());
                    addTexCoords(letter.getXTextureCoord(), letter.getYTextureCoord(),
                            letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
                    cursorX += letter.getXAdvance()*text.getFontSize()* Game.INSTANCE.getRatio();
                }
                cursorX += metaData.getSpaceWidth()*text.getFontSize()* Game.INSTANCE.getRatio();
            }
            cursorX = 0;
            cursorY += LINE_HEIGHT*text.getFontSize();
        }
    }

    private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize) {
        double x = cursorX + (character.getXOffset() * fontSize * Game.INSTANCE.getRatio());
        double y = cursorY + ((character.getYOffset() - LINE_HEIGHT) * fontSize);
        double maxX = x + (character.getSizeX() * fontSize * Game.INSTANCE.getRatio());
        double maxY = y + (character.getSizeY() * fontSize);
        addVertices(x, -y, maxX, -maxY);
    }

    private void addVertices(double x, double y, double maxX, double maxY) {
        translatedPoints[0]  = (float) x;
        translatedPoints[1]  = (float) y;
        translatedPoints[2]  = (float) x;
        translatedPoints[3]  = (float) maxY;
        translatedPoints[4]  = (float) maxX;
        translatedPoints[5]  = (float) maxY;
        translatedPoints[6]  = (float) maxX;
        translatedPoints[7]  = (float) maxY;
        translatedPoints[8]  = (float) maxX;
        translatedPoints[9]  = (float) y;
        translatedPoints[10] = (float) x;
        translatedPoints[11] = (float) y;
        bufferData.putVertices(translatedPoints);
    }

    private void addTexCoords(double x, double y, double maxX, double maxY) {
        translatedPoints[0]  = (float) x;
        translatedPoints[1]  = (float) y;
        translatedPoints[2]  = (float) x;
        translatedPoints[3]  = (float) maxY;
        translatedPoints[4]  = (float) maxX;
        translatedPoints[5]  = (float) maxY;
        translatedPoints[6]  = (float) maxX;
        translatedPoints[7]  = (float) maxY;
        translatedPoints[8]  = (float) maxX;
        translatedPoints[9]  = (float) y;
        translatedPoints[10] = (float) x;
        translatedPoints[11] = (float) y;
        bufferData.putTextureCoords(translatedPoints);
    }
}

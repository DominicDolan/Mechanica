package font;

import display.Game;

/**
 * Created by domin on 29 Mar 2017.
 */

public class TextMeshDynamicCreator {

    protected static final double LINE_HEIGHT = 0.03f;
    protected static final int SPACE_ASCII = 32;
    protected static final int RETURN = (int) '\n';
    private float[] translatedPoints;
    private TextBufferData bufferData;

    private Line[] savedLines = new Line[32];

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
        bufferData.set(text.getCharArrayString().length);
        createQuadVertices(text, updateStructure(text));
        bufferData.flip();
        return bufferData;
    }

    private int updateStructure(GUIText text){
        int currentLine = 0;
        savedLines[currentLine].set(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
        savedLines[currentLine].setCurrentWord();

        for (char c: text.getCharArrayString()){
            int ascii = (int) c;
            boolean returnCharacter = (ascii == RETURN);
            if (ascii == SPACE_ASCII || returnCharacter) {

                boolean added = false;
                if (!returnCharacter)
                    added = savedLines[currentLine].attemptToAddWord();
                if (!added || returnCharacter){
                    currentLine++;
                    savedLines[currentLine].set(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
                    savedLines[currentLine].attemptToAddWord();
                }
                continue;
            }

            savedLines[currentLine]
                    .getCurrentWord()
                    .addCharacter(metaData.getCharacter(ascii));
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

    private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize) {
        double x = curserX + (character.getXOffset() * fontSize * Game.INSTANCE.getRatio());
        double y = curserY + ((character.getYOffset() - LINE_HEIGHT) * fontSize);
        double maxX = x + (character.getSizeX() * fontSize * Game.INSTANCE.getRatio());
        double maxY = y + (character.getSizeY() * fontSize);
        double properX = (2 * x);
        double properY = (-2 * y) ;
        double properMaxX = (2 * maxX);
        double properMaxY = (-2 * maxY);
        addVertices(properX, properY, properMaxX, properMaxY);
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

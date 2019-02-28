package font;

import java.util.ArrayList;
import java.util.List;

/**
 * During the loading of a text this represents one word in the text.
 * @author Karl
 *
 */
public class Word {

	private Character[] savedCharacters = new Character[32];
	private int currentCharacter = 0;
	private List<Character> characters = new ArrayList<Character>();
	private double width = 0;
	private double fontSize;

	protected Word(){
		for (int i = 0; i < savedCharacters.length; i++) {
			savedCharacters[i] = new Character();
		}
	}

	protected void set(){
		width = 0;
		currentCharacter = 0;
		characters.clear();
	}

	protected void setFontSize(double fontSize){
		this.fontSize = fontSize;
	}

	/**
	 * Create a new empty word.
	 * @param fontSize - the font size of the text which this word is in.
	 */
	protected Word(double fontSize){
		this.fontSize = fontSize;
	}
	
	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 * @param character - the character to be added.
	 */
	protected void addCharacter(Character character){
		savedCharacters[currentCharacter] = character;
		characters.add(character);
		width += character.getXAdvance() * fontSize;
	}
	
	/**
	 * @return The list of characters in the word.
	 */
	protected List<Character> getCharacters(){
		return characters;
	}
	
	/**
	 * @return The width of the word in terms of screen size.
	 */
	protected double getWordWidth(){
		return width;
	}

}

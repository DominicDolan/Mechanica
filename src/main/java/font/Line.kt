package font

import java.util.*

/**
 * Represents a line of text during the loading of a text.
 *
 * @author Karl
 */
class Line() {

    /**
     * @return The max length of the line.
     */
    var maxLength: Double = 0.0
        private set
    private var spaceSize: Double = 0.0
    private val fontSize: Double = 0.0

    private val savedWords = Array(32) { Word(fontSize) }
    private var currentWord = 0
    private val words = ArrayList<Word>()
    /**
     * @return The current screen-space length of the line.
     */
    var lineLength = 0.0
        private set

    val numberOfWords: Int
        get() = currentWord + 1


    operator fun set(spaceWidth: Double, fontSize: Double, maxLength: Double) {
        currentWord = 0
        lineLength = 0.0
        this.spaceSize = spaceWidth * fontSize
        this.maxLength = maxLength
        for (i in savedWords.indices) {
            savedWords[i].setFontSize(fontSize)
        }
        savedWords[0].set()

    }

    fun addWord() {
        currentWord++
        savedWords[currentWord].set()
    }


    /**
     * Creates an empty line.
     *
     * @param spaceWidth
     * - the screen-space width of a space character.
     * @param fontSize
     * - the size of font being used.
     * @param maxLength
     * - the screen-space maximum length of a line.
     */
    constructor(spaceWidth: Double, fontSize: Double, maxLength: Double):this() {
        this.spaceSize = spaceWidth * fontSize
        this.maxLength = maxLength
    }

    /**
     * Attempt to add a word to the line. If the line can fit the word in
     * without reaching the maximum line length then the word is added and the
     * line length increased.
     *
     * @param word
     * - the word to try to add.
     * @return `true` if the word has successfully been added to the line.
     */
    fun attemptToAddWord(word: Word): Boolean {
        var additionalLength = word.wordWidth
        additionalLength += if (words.isNotEmpty()) spaceSize else 0.0
        return if (lineLength + additionalLength <= maxLength) {
            words.add(word)
            lineLength += additionalLength
            true
        } else {
            false
        }
    }

    fun attemptToAddWord(): Boolean {
        var additionalLength = savedWords[currentWord].wordWidth
        additionalLength += if (currentWord == 0) 0.0 else spaceSize
        return if (lineLength + additionalLength <= maxLength) {
            addWord()
            lineLength += additionalLength
            true
        } else {
            false
        }
    }

    fun setCurrentWord() {
        savedWords[currentWord].set()
    }


    fun getWord(word: Int): Word {
        return savedWords[word]
    }

    fun getCurrentWord(): Word {
        return savedWords[currentWord]
    }

    /**
     * @return The list of words in the line.
     */
    fun getWords(): List<Word> {
        return words
    }

}

@file:Suppress("unused") // There will be many functions here that go unused most of the time
package font

import graphics.Image
import models.Model

import java.nio.Buffer

/**
 * Represents a piece of text in the game.
 *
 * @author Karl
 */
class GUIText
/**
 * Creates a new text, loads the text's quads into a VAO, and adds the text
 * to the screen.
 *
 * @param text
 * - the text.
 * @param fontSize
 * - the font size of the text, where a font size of 1 is the
 * default size.
 * @param font
 * - the font that this text should use.
 * @param positionX
 * - the x position on the screen where the top left corner of the
 * text should be rendered. The top left corner of the screen is
 * (0, 0) and the bottom right is (1, 1).
 * @param positionY
 * - the y position on the screen where the top left corner of the
 * text should be rendered. The top left corner of the screen is
 * (0, 0) and the bottom right is (1, 1).
 * @param maxLineLength
 * - basically the width of the virtual page in terms of screen
 * width (1 is full screen width, 0.5 is half the width of the
 * screen, etc.) Text cannot go off the edge of the page, so if
 * the text is longer than this length it will go onto the next
 * line. When text is centered it is centered into the middle of
 * the line, based on this line length value.
 * @param centered
 * - whether the text should be centered or not.
 */
(text: String, fontSize: Float, font: FontType,
 var positionX: Float,
 var positionY: Float,
 maxLineLength: Float,
 centered: Boolean = false) {

    /**
     * @return The string of text.
     */
    var textString: String? = null
        private set

    var charArrayString: CharArray? = null
        private set
    /**
     * @return The total number of polygons of all the text's quads.
     */
    //	public int getVertexCount() {
    //		return this.vertexCount;
    //	}

    /**
     * @return the font size of the text (a font size of 1 is normal).
     */
    var fontSize: Float = 0.toFloat()

    //	private int vertexCount;
    var vertexBuffer: Buffer? = null
    var textureBuffer: Buffer? = null

    /**
     * Set the colour of the text.
     */
    var color: Long = 0
    /**
     * @return The maximum length of a line of this text.
     */
    var maxLineSize: Float = 0.toFloat()
        private set

    var numberOfLines: Int = 0

    /**
     * @return The font used by this text.
     */
    var font: FontType? = null
        private set
    val metaFile: MetaFile

    /**
     * @return `true` if the text should be centered.
     */
    var isCentered = false
        private set

    val textureAtlas: Image
        get() = font!!.textureAtlas

    init {
        this.textString = text
        this.charArrayString = text.toCharArray()
        this.fontSize = fontSize
        this.font = font
        this.metaFile = font.metaFile
        this.maxLineSize = maxLineLength
        this.isCentered = centered
    }

    fun set(text: String, fontSize: Float, font: FontType, x: Float, y: Float, maxLineLength: Float,
                     centered: Boolean) {
        this.textString = text
        this.charArrayString = text.toCharArray()
        this.fontSize = fontSize
        this.font = font
        this.positionX = x
        this.positionY = y
        this.maxLineSize = maxLineLength
        this.isCentered = centered
        set()
    }

    operator fun set(text: String, x: Float, y: Float) {
        this.textString = text
        this.charArrayString = text.toCharArray()
        this.positionX = x
        this.positionY = y
        set()
    }

    fun set() {

    }

    /**
     * Remove the text from the screen.
     */
    fun remove() {
        // remove text
    }

    fun setText(text: String) {
        this.textString = text
        set()
    }

    companion object {
        val dynamicCreator = TextMeshDynamicCreator()
    }
}

package font

/**
 * Simple data structure class holding information about a certain glyph in the
 * font texture atlas. All sizes are for a font-size of 1.
 *
 * @author Karl
 */
class Character {

    var id: Int = 0
        private set
    var xTextureCoord: Double = 0.toDouble()
    var yTextureCoord: Double = 0.toDouble()
    var xMaxTextureCoord: Double = 0.toDouble()
        private set
    var yMaxTextureCoord: Double = 0.toDouble()
        private set
    var xOffset: Double = 0.toDouble()
    var yOffset: Double = 0.toDouble()
    var sizeX: Double = 0.toDouble()
        private set
    var sizeY: Double = 0.toDouble()
        private set
    var xAdvance: Double = 0.toDouble()

    constructor() {

    }

    operator fun set(id: Int, xTextureCoord: Double, yTextureCoord: Double, xTexSize: Double, yTexSize: Double,
                               xOffset: Double, yOffset: Double, sizeX: Double, sizeY: Double, xAdvance: Double) {
        this.id = id
        this.xTextureCoord = xTextureCoord
        this.yTextureCoord = yTextureCoord
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.xMaxTextureCoord = xTexSize + xTextureCoord
        this.yMaxTextureCoord = yTexSize + yTextureCoord
        this.xAdvance = xAdvance
    }

    /**
     * @param id
     * - the ASCII value of the character.
     * @param xTextureCoord
     * - the x texture coordinate for the top left corner of the
     * character in the texture atlas.
     * @param yTextureCoord
     * - the y texture coordinate for the top left corner of the
     * character in the texture atlas.
     * @param xTexSize
     * - the width of the character in the texture atlas.
     * @param yTexSize
     * - the height of the character in the texture atlas.
     * @param xOffset
     * - the x distance from the curser to the left edge of the
     * character's quad.
     * @param yOffset
     * - the y distance from the curser to the top edge of the
     * character's quad.
     * @param sizeX
     * - the width of the character's quad in screen space.
     * @param sizeY
     * - the height of the character's quad in screen space.
     * @param xAdvance
     * - how far in pixels the cursor should advance after adding
     * this character.
     */
    constructor(id: Int, xTextureCoord: Double, yTextureCoord: Double, xTexSize: Double, yTexSize: Double,
                xOffset: Double, yOffset: Double, sizeX: Double, sizeY: Double, xAdvance: Double) {
        this.id = id
        this.xTextureCoord = xTextureCoord
        this.yTextureCoord = yTextureCoord
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.sizeX = sizeX
        this.sizeY = sizeY
        this.xMaxTextureCoord = xTexSize + xTextureCoord
        this.yMaxTextureCoord = yTexSize + yTextureCoord
        this.xAdvance = xAdvance
    }
}

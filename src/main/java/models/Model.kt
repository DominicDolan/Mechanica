package models

import org.lwjgl.opengl.GL11

/**
 * Created by domin on 26/10/2017.
 */
open class Model(val vaoID: Int, var vertexCount: Int, var texture: Int = -1, var drawType: Int = GL11.GL_TRIANGLE_FAN) {
    constructor(other: Model): this(other.vaoID, other.vertexCount, other.texture, other.drawType)

}
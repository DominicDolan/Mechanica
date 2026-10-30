package com.mechanica.engine.text

import com.mechanica.engine.shader.LwjglImage
import com.mechanica.engine.shaders.models.Image
import org.lwjgl.opengl.GL11

class FontAtlas(data: LwjglStandardFont.FontData, config: LwjglFontAtlasConfiguration) : Image {
    val width: Int = config.width
    val height: Int = config.height

    // Both STB's bitmap quads and our SDF quads already contain their glyph bearings.
    // One extra texel lets filtering reach the transparent gutter at the quad's edges.
    val padding: Float = 1f
    val scale: Double = 1.0 / (data.scale * config.characterSize)

    val distanceFieldEdge: Float?
    private val image: Image

    init {
        val bitmap = FontAtlasBitmap(data.ttf, data.info, data.cdata, config)
        distanceFieldEdge = bitmap.renderDistanceFieldEdge
        image = LwjglImage.create(bitmap.pixels, width, height, bitmap.mipLevels, GL11.GL_ALPHA, clampToEdge = true)
    }

    override val id: Int
        get() = image.id

    override fun bind() = image.bind()
}

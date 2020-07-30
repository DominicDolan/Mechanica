package com.mechanica.engine.text

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.unit.vector.DynamicVector
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class LwjglStandardFont(resource: Resource, initializer: FontAtlasConfiguration.() -> Unit): Font() {

    override val ascent: Float
        get() = metrics.ascent
    override val descent: Float
        get() = metrics.descent
    override val lineGap: Float
        get() = metrics.lineGap

    private val config = LwjglFontAtlasConfiguration(initializer)
    private val charRange = config.charRange.first.toInt()..config.charRange.last.toInt()
    private val unknownIndex = 128 - charRange.first

    private val data = FontData(resource)

    override val atlas = FontAtlas(data, config)

    private val metrics = FontMetrics(data.info, data.scale)

    override fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<DynamicVector>, texCoords: Array<DynamicVector>) {
        val c = cursor.currentChar
        val atlasScale = atlas.scale
        val dataScale = data.scale
        
        val kern = stbtt_GetCodepointKernAdvance(data.info, cursor.previousChar.toInt(), c.toInt()).toFloat()/atlasScale.toFloat()
        val charAsInt = if (c.toInt() in charRange) c.toInt() - charRange.first else unknownIndex
        
        MemoryStack.stackPush().use { stack ->
            val x = stack.floats(cursor.xAdvance/(dataScale*atlasScale).toFloat() + kern)
            val y = stack.floats(cursor.yAdvance)
            val q = STBTTAlignedQuad.mallocStack(stack)

            STBTruetype.stbtt_GetPackedQuad(data.cdata, atlas.width, atlas.height, charAsInt, x, y, q, true)
            cursor.xAdvance = (x[0]*dataScale*atlasScale).toFloat()

            if (c != ' ') q.copyToArrays(cursor, dataScale*atlasScale, atlas, positions, texCoords)
        }
    }

    inner class FontData(resource: Resource) {
        val ttf: ByteBuffer = resource.buffer
        val info: STBTTFontinfo = STBTTFontinfo.create()
        val cdata: STBTTPackedchar.Buffer
        val scale: Float

        init {
            check(STBTruetype.stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }
            cdata = STBTTPackedchar.create(charRange.count())
            scale = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)
        }
    }

}
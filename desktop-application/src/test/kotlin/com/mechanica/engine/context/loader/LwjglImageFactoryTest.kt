package com.mechanica.engine.context.loader

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import com.mechanica.engine.shaders.context.ImageDimensions

class LwjglImageFactoryTest {
    @Test
    fun readsDimensionsWithoutAnOpenGLContext() {
        val output = ByteArrayOutputStream()
        ImageIO.write(BufferedImage(7, 3, BufferedImage.TYPE_INT_ARGB), "png", output)
        val factory = LwjglImageFactory()
        assertEquals(ImageDimensions(7, 3), factory.dimensions(output.toByteArray()))
        assertFailsWith<IllegalArgumentException> { factory.dimensions(byteArrayOf(1, 2, 3)) }
    }
}

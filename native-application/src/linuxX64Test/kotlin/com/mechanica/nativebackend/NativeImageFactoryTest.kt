@file:OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.context.ImageDimensions
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NativeImageFactoryTest {
    @Test
    fun readsImageDimensionsWithoutAContext() {
        val image = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIHWP4z8DwHwAFgAI/ScLttAAAAABJRU5ErkJggg==")
        val factory = NativeImageFactory()
        assertEquals(ImageDimensions(1, 1), factory.dimensions(image))
        assertFailsWith<IllegalArgumentException> { factory.dimensions(byteArrayOf(1, 2, 3)) }
    }
}

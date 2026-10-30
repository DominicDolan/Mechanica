package com.mechanica.engine.resources

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ResourceTest {
    private fun resource(text: String): Resource = object : Resource {
        override val path = "test"
        override fun readBytes() = text.encodeToByteArray()
    }

    @Test
    fun textReadsHandleLineEndingsAndEmptyFiles() {
        assertEquals(emptyList(), resource("").lines)
        assertEquals(listOf("one", "two"), resource("one\r\ntwo\n").lines)
        assertEquals("one\ntwo\n", resource("one\r\ntwo\n").contents)
        assertContentEquals("café".encodeToByteArray(), resource("café").readBytes())
    }
}

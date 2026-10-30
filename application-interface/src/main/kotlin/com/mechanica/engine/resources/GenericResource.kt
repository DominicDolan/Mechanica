package com.mechanica.engine.resources

interface GenericResource {
    val path: String
    fun readBytes(): ByteArray
    val lines: List<String>
    val contents: String
}

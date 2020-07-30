package com.mechanica.engine.resources

import java.io.InputStream
import java.nio.Buffer

interface GenericResource {
    val path: String
    val stream: InputStream
    val lines: List<String>
    val contents: String
    val buffer: Buffer
}
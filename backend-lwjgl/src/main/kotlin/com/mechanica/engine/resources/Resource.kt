package com.mechanica.engine.resources

import org.lwjgl.BufferUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import kotlin.streams.toList

interface Resource {
    val path: String
    val stream: InputStream
    val lines: List<String>
        get() {
            val reader = BufferedReader(InputStreamReader(stream))
            val lines = reader.lines().toList()
            reader.close()
            return lines
        }
    val contents: String
        get() {
            val sb = StringBuilder()
            lines.forEach {
                sb.appendln(it)
            }
            return sb.toString()
        }
    val buffer: ByteBuffer
        get() {
            val bytes = stream.readAllBytes()
            val buffer = BufferUtils.createByteBuffer(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return buffer
        }

    companion object {

        operator fun invoke(file: String): Resource {
            val fileForURL = file.replace("\\", "/")
            val url = getResourceURL(fileForURL) ?: throw IllegalStateException("Resource not found at $fileForURL")

            return ResourceImpl(url)
        }

        operator fun invoke(url: URL): Resource {
            return ResourceImpl(url)
        }

        operator fun invoke(uri: URI): Resource {
            return ResourceImpl(uri.toURL())
        }

        fun external(file: String): Resource {
            return ExternalResource(file)
        }

        private fun getResourceURL(file: String): URL? {
            val systemResources = ClassLoader.getSystemResources(file)
            return if (systemResources != null && systemResources.hasMoreElements())
                systemResources.nextElement()
            else null
        }

        private class ResourceImpl(private val url: URL) : Resource {
            private val protocol = url.protocol
            override val path: String
                get() {
                    return if (protocol == "jar") {
                        URL(url.path).path
                    } else {
                        url.path
                    }
                }
            override val stream: InputStream
                get() {
                    val conn = url.openConnection()
                    return conn.getInputStream()
                }

        }

    }
}
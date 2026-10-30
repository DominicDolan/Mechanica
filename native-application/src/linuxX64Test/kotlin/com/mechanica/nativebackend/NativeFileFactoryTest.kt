@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.resources.ResourceNotFoundException
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.test.*

class NativeFileFactoryTest {
    @Test
    fun readsWritesAndListsResources() = memScoped {
        val template = "/tmp/mechanica-native-test-XXXXXX".cstr.getPointer(this)
        val root = requireNotNull(mkdtemp(template)).toKString()
        val files = NativeFileFactory(root, root)
        try {
            assertFailsWith<ResourceNotFoundException> { files.externalResource("missing", false).readBytes() }
            val file = files.externalResource("sub/data.txt", true)
            file.write("hello café")
            assertEquals("hello café", file.readBytes().decodeToString())
            assertEquals(listOf("sub"), files.directory(".").folders.map { it.name })
            val tree = files.directory(".", true)
            assertEquals(listOf("data.txt"), tree.folders.single().resources.map { it.path.substringAfterLast('/') })
        } finally {
            unlink("$root/sub/data.txt")
            rmdir("$root/sub")
            rmdir(root)
        }
    }
}

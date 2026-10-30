@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
import kotlinx.cinterop.*
import osmesa.*

fun main() = memScoped {
    val context = OSMesaCreateContextExt(OSMESA_RGBA.toUInt(), 16, 0, 0, null) ?: error("OSMesa context creation failed")
    val pixels = allocArray<ByteVar>(4 * 4 * 4)
    check(OSMesaMakeCurrent(context, pixels, GL_UNSIGNED_BYTE.toUInt(), 4, 4) != 0) { "OSMesaMakeCurrent failed" }
    glClearColor(1f, 0f, 0f, 1f)
    glClear(GL_COLOR_BUFFER_BIT.toUInt())
    glFinish()
    val red = pixels[0].toInt() and 255
    val green = pixels[1].toInt() and 255
    val blue = pixels[2].toInt() and 255
    println("OpenGL pixel: $red, $green, $blue")
    check(red == 255 && green == 0 && blue == 0)
    OSMesaDestroyContext(context)
}

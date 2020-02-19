package gl.utils

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import java.util.*


object GLVersion {
    private val versions = Versions(GL11.glGetString(GL11.GL_VERSION) ?: "N/A")
    private val extensionMap: MutableMap<String?, Boolean?>

    val majorVersion: Int = versions.majorVersion
    val minorVersion: Int = versions.minorVersion
    val version: Double = versions.version

//        fun isExtensionSupported(extension: GLExtension): Boolean {
//            return isExtensionSupported(extension.asString())
//        }

    fun isExtensionSupported(extension: String): Boolean {
        return extensionMap.containsKey(extension)
    }

    val supportedExtensions: Collection<String?>
        get() = extensionMap.keys

    init {
        val supportedExtensions: Array<String?>
        if (majorVersion >= 3) {
            val numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS)
            supportedExtensions = arrayOfNulls(numExtensions)
            for (i in 0 until numExtensions) {
                supportedExtensions[i] = GL30.glGetStringi(GL11.GL_EXTENSIONS, i)
            }
        } else {
            val extensionsAsString = GL11.glGetString(GL11.GL_EXTENSIONS)
            supportedExtensions = extensionsAsString!!.split(" ").toTypedArray()
        }
        extensionMap = HashMap()
        for (extension in supportedExtensions) {
            extensionMap[extension] = java.lang.Boolean.TRUE
        }
    }

    private class Versions(versionString: String) {
        val majorVersion: Int
        val minorVersion: Int
        val version: Double

        init {
            val majorVersionIndex = versionString.indexOf('.')
            var minorVersionIndex = majorVersionIndex + 1
            while (minorVersionIndex < versionString.length && Character.isDigit(minorVersionIndex)) {
                minorVersionIndex++
            }
            minorVersionIndex++
            majorVersion = versionString.substring(0, majorVersionIndex).toInt()
            minorVersion = versionString.substring(majorVersionIndex + 1, minorVersionIndex).toInt()
            version = versionString.substring(0, minorVersionIndex).toDouble()
        }
    }
}
package resources

import display.Game
import java.io.File
import java.util.*

object Res {
    class SpecificResource(private val prefix: String, private val extension: String) {
        operator fun get(file: String): String {
            val fileFixed = fixExtension(file)
            return getResource("$prefix$fileFixed")
        }

        private fun fixExtension(file: String): String {
            val lastSlash = file.lastIndexOf("/")
            val dotIndex = file.lastIndexOf(".")
            val hasExtension = (dotIndex > lastSlash) || (lastSlash == -1 && dotIndex != -1)
            return if (hasExtension) file else "$file.$extension"
        }
    }

    val svg = SpecificResource("res/svg/", "svg")
    val image = SpecificResource("res/images/", "png")
    val font = SpecificResource("res/fonts/", "png")
    val animations = SpecificResource("res/animations/", "")

    private const val relativeLocation = "../../../resources/main/"
    private val location get() =
        this.javaClass.getResource(relativeLocation)?.path?.toString() ?:
        ClassLoader.getSystemResources(".").toList().first { it.path.contains("resource", true) }?.path ?:
        throw IllegalStateException("No resource directory was found on the class path")

    operator fun get(file: String): String {
        return getResource("res/$file")
    }

    private fun getResource(file: String) : String {
        val systemResources = ClassLoader.getSystemResources(file)
        val resource =
                if (systemResources != null && systemResources.hasMoreElements())
                    systemResources.nextElement()
                else null

        return if (resource == null || resource.path == null) {
            val errorMessage = "Resource not found at location: $location$file"
            if (Game.debug) throw MissingResourceException(errorMessage, location, file)
            System.err.println("$errorMessage, attempting to continue anyway")

            "$location$file"
        } else {
            resource.path
        }
    }
}
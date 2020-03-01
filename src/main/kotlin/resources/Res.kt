package resources

import display.Game

object Res {
    class SpecificResource(private val prefix: String, private val extension: String) {
        operator fun get(file: String): Resource {
            val fileFixed = fixExtension(file)
            return Resource("$prefix$fileFixed")
        }

        private fun fixExtension(file: String): String {
            val lastSlash = file.lastIndexOf("/")
            val dotIndex = file.lastIndexOf(".")
            val hasExtension = (dotIndex > lastSlash) || (lastSlash == -1 && dotIndex != -1)
            return if (hasExtension) file else "$file.$extension"
        }
    }

    class SpecificResourceDirectory(private val prefix: String) {
        operator fun get(file: String): ResourceDirectory {
            return ResourceDirectory("$prefix$file")
        }
    }

    val svg = SpecificResource("res/svg/", "svg")
    val image = SpecificResource("res/images/", "png")
    val font = SpecificResource("res/fonts/", "png")
    val animations = SpecificResourceDirectory("/res/animations/")

    fun external(path: String, createIfAbsent: Boolean = false): ExternalResource {
        val prefix = "res/"
        return ExternalResource("$prefix$path", createIfAbsent)
    }

    operator fun get(file: String): Resource {
        return Resource("res/$file")
    }
}
package resources

import org.intellij.lang.annotations.Language

object Res {
    class SpecificResource(private val prefix: String, private val extension: String) {
        operator fun get(file: String): String {
            val fileFixed = fixExtension(file)
            return this.javaClass.classLoader.getResource("$prefix$fileFixed").file
//            return Thread.currentThread().contextClassLoader.getResource("$prefix$fileFixed").file
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

    operator fun get(file: String): String {
        return this.javaClass.classLoader.getResource("res/$file").file
    }
}
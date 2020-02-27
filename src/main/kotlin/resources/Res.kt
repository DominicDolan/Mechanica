package resources

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

    val svg = SpecificResource("res/svg/", "svg")
    val image = SpecificResource("res/images/", "png")
    val font = SpecificResource("res/fonts/", "png")
    fun animations(directory: String) = ResourceDirectory("/res/animations/$directory")

    operator fun get(file: String): Resource {
        return Resource("res/$file")
    }
}
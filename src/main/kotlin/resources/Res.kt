package resources

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

    private val relativeLocation = "../../../resources/main/"
    private val location get() = this.javaClass.getResource(relativeLocation)?.path?.toString() ?: ""

    operator fun get(file: String): String {
        return getResource("res/$file")
    }

    private fun getResource(file: String) : String {
        println("Location: $location")
        val resource = this.javaClass.classLoader.getResource(relativeLocation + file)
        return if (resource == null) {
            System.err.println("Resource not found at location: $location$file")
            "$location$file"
        } else {
            resource.file ?: "$location$file"
        }
    }
}
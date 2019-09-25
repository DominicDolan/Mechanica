package resources

object Res {
    class SpecificResource(private val prefix: String, private val extension: String) {
        operator fun get(file: String): String {
            val fileFixed = fixExtension(file)
            val resource = this.javaClass.classLoader.getResource("$prefix$fileFixed")
            return resource?.file ?: "$location/$file"
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

    private val location get() = this.javaClass.classLoader.getResource("res")?.path?.toString() ?: ""

    operator fun get(file: String): String {
        val resource = this.javaClass.classLoader.getResource("res/$file")
        return resource?.file ?: "$location/$file"
    }
}
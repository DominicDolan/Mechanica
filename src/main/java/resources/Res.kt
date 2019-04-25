package resources

object Res {
    class SpecificResource(private val prefix: String, private val extension: String) {
        operator fun get(file: String): String {
            val fileFixed = fixExtension(file)
            return this.javaClass.classLoader.getResource("$prefix$fileFixed").file
        }

        private fun fixExtension(file: String): String {
            val index = file.lastIndexOf('.', file.lastIndexOf("/"))
            return if (index != -1) file else "$file.$extension"
        }
    }

    val svg = SpecificResource("res/svg/", "svg")
    val image = SpecificResource("res/images/", "png")

    operator fun get(file: String): String {
        return this.javaClass.classLoader.getResource("res/$file").file
    }
}
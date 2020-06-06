package com.mechanica.engine.resources

object Res {
    class SpecificResource(prefix: String, extension: String): SpecificGenericResource<Resource>(prefix, extension) {
        override fun returnResource(fixedFile: String) = Resource(fixedFile)
    }

    class SpecificAudioResource(prefix: String,  extension: String) : SpecificGenericResource<AudioResource>(prefix, extension) {
        override fun returnResource(fixedFile: String) = AudioResource(fixedFile)
    }

    class SpecificResourceDirectory(private val prefix: String) {
        operator fun get(file: String): ResourceDirectory {
            return ResourceDirectory("$prefix$file")
        }
    }

    val svg = SpecificResource("res/svg/", "svg")
    val image = SpecificResource("res/images/", "png")
    val font = SpecificResource("res/fonts/", "ttf")
    val audio = SpecificAudioResource("res/audio/", "ogg")
    val animations = SpecificResourceDirectory("/res/animations/")

    fun external(path: String, createIfAbsent: Boolean = false): ExternalResource {
        val prefix = "res/"
        return ExternalResource("$prefix$path", createIfAbsent)
    }

    operator fun get(file: String): Resource {
        return invoke(file)
    }

    operator fun invoke(file: String): Resource {
        return Resource("res/$file")
    }
}
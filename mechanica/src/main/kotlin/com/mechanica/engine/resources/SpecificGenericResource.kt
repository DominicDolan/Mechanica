package com.mechanica.engine.resources

abstract class SpecificGenericResource<R: GenericResource>(private val prefix: String, private val extension: String) {
    operator fun get(file: String): R = invoke(file)
    operator fun invoke(file: String): R {
        val fileFixed = fixExtension(file)
        return returnResource("$prefix$fileFixed")
    }

    protected abstract fun returnResource(fixedFile: String): R

    private fun fixExtension(file: String): String {
        val lastSlash = file.lastIndexOf("/")
        val dotIndex = file.lastIndexOf(".")
        val hasExtension = (dotIndex > lastSlash) || (lastSlash == -1 && dotIndex != -1)
        return if (hasExtension) file else "$file.$extension"
    }
}
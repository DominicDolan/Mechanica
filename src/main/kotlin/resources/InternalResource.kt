package resources

import display.Game
import java.io.*

class InternalResource(file: String) : Resource {

    private val isJar: Boolean
    private val resource: Resource

    override val path: String
        get() = resource.path
    override val stream: InputStream
        get() = resource.stream

    init {
        val codeSourcePath = Game::class.java.protectionDomain.codeSource.location.path
        isJar = codeSourcePath.endsWith(".jar", true)
                || codeSourcePath.contains(".jar/")
                || codeSourcePath.contains(".jar!/")

        resource = if (isJar) InternalJarResource(file) else InternalFileResource(file)
    }
}
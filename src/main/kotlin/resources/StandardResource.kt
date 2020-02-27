package resources

import display.Game
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL
import java.util.*

class StandardResource(private val file: String) : Resource {

    private val relativeLocation = "../../../resources/main/"
    private val possibleLocation get() =
        this.javaClass.getResource(relativeLocation)?.path?.toString() ?:
        ClassLoader.getSystemResources(".").toList().firstOrNull { it.path.contains("resource", true) }?.path ?:
        throw IllegalStateException("No resource directory was found on the class path")
    private val url : URL?
        get() {
            val systemResources = ClassLoader.getSystemResources(file)
            return if (systemResources != null && systemResources.hasMoreElements())
                        systemResources.nextElement()
                    else null

        }

    override val path: String
        get() {
            val url = this.url
            return if (url == null || url.path == null) {
                val fileCheck = File(file)
                if (fileCheck.exists()) {
                    return fileCheck.absolutePath
                }

                val resourceErrorMessage = "Resource not found at location: $possibleLocation$file or at location ${fileCheck.absolutePath}"
                if (Game.debug) throw MissingResourceException(resourceErrorMessage, possibleLocation, file)
                System.err.println("$resourceErrorMessage, attempting to continue anyway")

                "$possibleLocation$file"
            } else {
                url.path
            }
        }
    override val stream: InputStream
        get() {
            val file = File(path)
            return FileInputStream(file)
//            val conn = url?.openConnection()
//            return conn?.getInputStream() ?: throw IllegalStateException("URL is null")
        }

    private fun getURL() {

    }
}
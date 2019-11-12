package resources

import display.Game
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class StandardResource(private val file: String) : Resource {

    private val relativeLocation = "../../../resources/main/"
    private val possibleLocation get() =
        this.javaClass.getResource(relativeLocation)?.path?.toString() ?:
        ClassLoader.getSystemResources(".").toList().first { it.path.contains("resource", true) }?.path ?:
        throw IllegalStateException("No resource directory was found on the class path")


    override val path: String
        get() {
            val systemResources = ClassLoader.getSystemResources(file)
            val resourceURL =
                    if (systemResources != null && systemResources.hasMoreElements())
                        systemResources.nextElement()
                    else null

            return if (resourceURL == null || resourceURL.path == null) {
                val errorMessage = "Resource not found at location: $possibleLocation$file"
                if (Game.debug) throw MissingResourceException(errorMessage, possibleLocation, file)
                System.err.println("$errorMessage, attempting to continue anyway")

                "$possibleLocation$file"
            } else {
                resourceURL.path
            }
        }
    override val stream: InputStream
        get() {
            val file = File(path)
            return FileInputStream(file)
        }
}
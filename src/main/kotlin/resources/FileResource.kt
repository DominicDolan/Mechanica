package resources

import display.Game
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class FileResource(private val file: String) : Resource {
    override val path: String
        get() {
            val systemResources = ClassLoader.getSystemResources(file)
            val resourceURL =
                    if (systemResources != null && systemResources.hasMoreElements())
                        systemResources.nextElement()
                    else null

            return if (resourceURL == null || resourceURL.path == null) {
                val fileCheck = File(file)
                if (fileCheck.exists()) {
                    return fileCheck.absolutePath
                }

                val resourceErrorMessage = "Resource not found at location: ${fileCheck.absolutePath}"

                throw MissingResourceException(resourceErrorMessage, fileCheck.absolutePath, file)
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
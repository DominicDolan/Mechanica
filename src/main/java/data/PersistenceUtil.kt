package data

import resources.Res
import java.io.File
import java.util.ArrayList
import java.lang.reflect.Method
import java.io.IOException
import java.io.BufferedWriter
import java.io.FileWriter
import java.lang.StringBuilder

fun saveData(dataObj: Any) {
    val getters = ArrayList<Method>()
    val allMethods = dataObj::class.java.methods
    for (method in allMethods) {
        if (method.name.startsWith("get") && method.name != "getClass") {
            getters.add(method)
        }
    }
    println("\nAll Fields: ")
    getters.forEach { println("Name: ${it.name}, Type: ${it.returnType}, Value: ${it.invoke(dataObj)}") }
    val directory = File(Res["data"])
    if (!directory.exists()) {
        directory.mkdir()
    }
    val contentBuilder = StringBuilder()

    for (method in getters) {
        contentBuilder
                .append(method.name.replace("get", "")).append(",")
                .append(method.returnType).append(",")
                .append(method.invoke(dataObj)).append("\n")
    }
    val content = contentBuilder.toString()
    val file = File(directory.absolutePath + "\\" + dataObj::class.java.name + ".txt")
    try {
        val fw = FileWriter(file.absoluteFile)
        val bw = BufferedWriter(fw)
        bw.write(content)
        bw.close()
    } catch (e: IOException) {
        e.printStackTrace()
        System.exit(-1)
    }

}

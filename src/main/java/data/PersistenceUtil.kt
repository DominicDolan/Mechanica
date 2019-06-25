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
    getters.forEach { println("Name: ${it.name}, Type: ${it.returnType}, Value: ${it.invoke(dataObj)}, Camel case: ${it.camelCaseName}") }

    val contentBuilder = StringBuilder()

    for (method in getters) {
        contentBuilder
                .append(method.name.replace("get", "")).append(",")
                .append(method.returnType).append(",")
                .append(method.invoke(dataObj)).append("\n")
    }
    val content = contentBuilder.toString()

    val file = File(dataObj.dataFile)
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

fun loadData(dataObj: Any) {
    data class Variable(val name: String, val type: String, val value: String)
    val vars = ArrayList<Variable>()

    File(dataObj.dataFile).forEachLine {
        val params = it.split(",")
        vars.add(Variable(params[0], params[1], params[2]))
    }
    val setters = HashMap<String, Method>()
    val allMethods = dataObj::class.java.methods
    for (method in allMethods) {
        if (method.name.startsWith("set") && method.name != "getClass") {
            setters.put(method.name, method)
        }
    }
    println("\nAll Fields: ")
//    setters.forEach { it.invoke(dataObj) }



}

private val Method.camelCaseName: String
    get() = this.name
            .replace("get", "")
            .replace("set", "")
            .replace(0..1) { it.toLowerCase() + this }


private fun String.replace(range: IntRange, function: String.(String) -> String): String {
    val rangeString = this.substring(range)
    val removed = this.removeRange(range)
    return removed.function(rangeString)
}

private val directory: String get() {
    val directory = File(Res["data"])
    if (!directory.exists()) {
        directory.mkdir()
    }
    return directory.absolutePath
}

private val Any.dataFile get() = directory + sep + this.name + ".txt"

private val Any.name get() = this::class.java.name

private val sep get() = File.separator

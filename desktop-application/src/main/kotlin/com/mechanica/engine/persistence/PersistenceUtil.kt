package com.mechanica.engine.persistence

import com.mechanica.engine.resources.ExternalResource
import java.io.File
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.forEach
import kotlin.collections.set

fun saveData(dataObj: Any) {
    val getters = ArrayList<Method>()
    for (method in dataObj.methods) {
        if (method.name.startsWith("get") && method.name != "getClass") {
            getters.add(method)
        }
    }

    val contentBuilder = StringBuilder()

    for (method in getters) {
        contentBuilder
                .append(method.camelCaseName).append(",")
                .append(method.returnType.simpleName).append(",")
                .append(method.invoke(dataObj)).append("\n")
    }
    val content = contentBuilder.toString()

    val file = resource(dataObj.dataFile, true)
    file.write(content)

}

fun loadData(dataObj: Any) {
    data class Variable(val name: String, val type: String, val value: String, val setter: Method?, val getter: Method?)
    fun Variable.set() {
        if (this.setter != null) {
            when (this.type) {
                "int" -> setter.invoke(dataObj, value.toIntOrNull()?: getter?.invoke(dataObj)?: 0)
                "double" -> setter.invoke(dataObj, value.toDoubleOrNull()?: getter?.invoke(dataObj)?: 0.0)
                "float" -> setter.invoke(dataObj, value.toFloatOrNull()?: getter?.invoke(dataObj)?: 0f)
                "long" -> setter.invoke(dataObj, value.toLongOrNull()?: getter?.invoke(dataObj)?: 0L)
                "boolean" -> setter.invoke(dataObj, value.toBoolean())
                "String" -> setter.invoke(dataObj, value)
            }
        }
    }

    val setters = getSetters(dataObj)
    val getters = getGetters(dataObj)

    val dataFile = resource(dataObj.dataFile, true)


    dataFile.lines.forEach {
        val params = it.split(",")
        val name = params[0]
        val v = Variable(name, params[1], params[2], setters[name], getters[name])
        v.set()
    }

}

private fun getGetters(dataObj: Any): HashMap<String, Method> {
    val getters = HashMap<String, Method>()
    for (method in dataObj.methods) {
        if (method.name.startsWith("get") && method.name != "getClass") {
            getters[method.camelCaseName] = method
        }
    }

    return getters
}


private fun getSetters(dataObj: Any): HashMap<String, Method> {
    val setters = HashMap<String, Method>()
    for (method in dataObj.methods) {
        if (method.name.startsWith("set")) {
            setters[method.camelCaseName] = method
        }
    }

    return setters
}

private fun resource(path: String, createIfAbsent: Boolean = false): ExternalResource {
    val prefix = "res/"
    return ExternalResource("$prefix$path", createIfAbsent)
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

private const val directory: String = "data"

private val Any.methods get() = this::class.java.methods

private val Any.dataFile get() = directory + sep + this.name + ".txt"

private val Any.name get() = this::class.java.name

private val sep get() = File.separator

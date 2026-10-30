package com.mechanica.engine.util

fun getCallingClass(): Class<*> = Class.forName(Exception().stackTrace.last().className)

fun calculateIsJar(): Boolean {
    val callingClass = getCallingClass()
    return callingClass.getResource("${callingClass.simpleName}.class").protocol == "jar"
}

fun calculateResourceDirectory(): String {
    val callingClass = getCallingClass()
    return callingClass.getResource("${callingClass.simpleName}.class").path
}

package util

import java.lang.Exception

fun calculateIsJar(): Boolean {
    val callingClass = getCallingClass()
    val simpleName = callingClass.simpleName
    val protocol = callingClass.getResource("$simpleName.class").protocol

    return  protocol == "jar"
}

fun calculateResourceDirectory(): String {
    val callingClass = getCallingClass()
    val simpleName = callingClass.simpleName
    val path = callingClass.getResource("$simpleName.class").path
    return path
}

private fun getCallingClass(): Class<*> {
    val stack = Exception().stackTrace
    return Class.forName(stack.last().className)
}
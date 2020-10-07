package com.mechanica.engine.util

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

fun getCallingClass(): Class<*> {
    val stack = Exception().stackTrace
    return Class.forName(stack.last().className)
}

fun codepointToString(cp: Int): String {
    val sb = StringBuilder()
    when {
        Character.isBmpCodePoint(cp) -> {
            sb.append(cp.toChar())
        }
        Character.isValidCodePoint(cp) -> {
            sb.append(Character.highSurrogate(cp))
            sb.append(Character.lowSurrogate(cp))
        }
        else -> {
            sb.append('?')
        }
    }
    return sb.toString()
}

fun scriptWithLineNumbers(script: String): String {
    val sb = StringBuilder()
    val lines = script.split("\n")
    for (i in lines.indices) {
        sb.append("${i + 1}  ${lines[i]}\n")
    }
    return sb.toString()
}
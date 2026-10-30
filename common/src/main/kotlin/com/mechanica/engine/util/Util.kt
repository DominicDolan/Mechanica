package com.mechanica.engine.util

fun codepointToString(cp: Int): String = when (cp) {
    in 0..0xFFFF -> cp.toChar().toString()
    in 0x10000..0x10FFFF -> {
        val value = cp - 0x10000
        "${(0xD800 + (value shr 10)).toChar()}${(0xDC00 + (value and 0x3FF)).toChar()}"
    }
    else -> "?"
}

fun scriptWithLineNumbers(script: String): String {
    val sb = StringBuilder()
    val lines = script.split("\n")
    for (i in lines.indices) {
        sb.append("${i + 1}  ${lines[i]}\n")
    }
    return sb.toString()
}
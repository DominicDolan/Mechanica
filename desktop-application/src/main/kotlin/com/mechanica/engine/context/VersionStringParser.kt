package com.mechanica.engine.context

class VersionStringParser(versionString: String) {
    val majorVersion: Int
    val minorVersion: Int
    val version: Double

    init {
        val majorVersionIndex = versionString.indexOf('.')
        var minorVersionIndex = majorVersionIndex + 1
        while (minorVersionIndex < versionString.length && Character.isDigit(minorVersionIndex)) {
            minorVersionIndex++
        }
        minorVersionIndex++
        majorVersion = versionString.substring(0, majorVersionIndex).toInt()
        minorVersion = versionString.substring(majorVersionIndex + 1, minorVersionIndex).toInt()
        version = versionString.substring(0, minorVersionIndex).toDouble()

    }
}
package com.mechanica.engine.context

interface Version {
    val majorVersion: Int
    val minorVersion: Int
    val version: String
        get() = "$majorVersion.$minorVersion"
}
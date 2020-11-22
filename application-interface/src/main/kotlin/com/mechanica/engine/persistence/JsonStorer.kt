package com.mechanica.engine.persistence

interface JsonStorer {
    fun getJson(): String
    fun storeJson(json: String)
}

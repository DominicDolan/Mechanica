package com.mechanica.engine.util

class GenericGameBoolean(private val condition: () -> Boolean) : GameBoolean() {
    override fun condition(): Boolean {
        return condition.invoke()
    }
}
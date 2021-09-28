package com.mechanica.engine.scenes.activation

interface Activatable {
    val active: Boolean get() = true

    /**
     * A function to be overridden which will run while [active] is set to false
     */
    fun whileInactive(delta: Double) {}
}


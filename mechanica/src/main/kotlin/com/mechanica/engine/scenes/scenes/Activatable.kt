package com.mechanica.engine.scenes.scenes

interface Activatable {
    var active: Boolean
        get() = true
        set(@Suppress("UNUSED_PARAMETER") value) {}


    /**
     * Function to be overriden that will be called immediately after [active] has been set to true
     */
    fun onActivate() {}

    /**
     * Function to be overriden that will be called immediately after [active] has been set to false
     */
    fun onDeactivate() {}

    /**
     * A function to be overridden which will run while [active] is set to false
     */
    fun whileInactive(delta: Double) {}
}

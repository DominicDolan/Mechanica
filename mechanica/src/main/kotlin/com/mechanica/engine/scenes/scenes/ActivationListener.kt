package com.mechanica.engine.scenes.scenes

import kotlin.reflect.KProperty

internal class ActivationListener {
    private val activationListeners = ArrayList<ActivationChangedEvents>()
    private var active = true
    var hasInitialized = false

    init {
        addListener(0) {
            active = it
        }
    }
    operator fun getValue(thisRef: SceneHub, property: KProperty<*>): Boolean {
        return active
    }

    operator fun setValue(thisRef: SceneHub, property: KProperty<*>, value: Boolean) {
        val hasChanged = active != value
        if (hasChanged) {
            runCallbacks(value)
        }
    }

    fun addListener(priority: Int = 0, listener: (Boolean) -> Unit) {
        activationListeners.add(ActivationChangedEvents(priority, listener))
        activationListeners.sortByDescending { it.priority }
    }

    fun runOnce() {
        if (!hasInitialized) runCallbacks(active)
    }

    private fun runCallbacks(activate: Boolean) {
        hasInitialized = true
        for (i in activationListeners.indices) {
            activationListeners[i].listener.invoke(activate)
        }
    }

    private class ActivationChangedEvents(val priority: Int, val listener: (Boolean) -> Unit)
}
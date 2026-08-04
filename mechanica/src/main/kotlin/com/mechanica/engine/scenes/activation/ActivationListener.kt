package com.mechanica.engine.scenes.activation

import kotlin.reflect.KProperty

class ActivationListener {
    private val activationListeners = ArrayList<(Boolean) -> Unit>()
    private var active = true

    fun addListener(listener: (Boolean) -> Unit) {
        activationListeners.add(listener)
    }

    operator fun getValue(thisRef: ActiveState, property: KProperty<*>): Boolean {
        return active
    }

    operator fun setValue(thisRef: ActiveState, property: KProperty<*>, value: Boolean) {
        if (active == value) return

        // Listeners run in registration order, before the value is written, so they still see
        // the state that is being left behind.
        for (i in activationListeners.indices) {
            activationListeners[i].invoke(value)
        }

        active = value
        if (value) thisRef.onActivate()
        else thisRef.onDeactivate()
    }
}

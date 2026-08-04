package com.mechanica.engine.scenes.activation

import kotlin.reflect.KProperty

class ActivationListener {
    private val activationListeners = ArrayList<ActivationChangedEvents>()
    private var active = true
    private var zeroPriorityIndex = 0

    fun addListener(priority: Int = 0, listener: (Boolean) -> Unit) {
        activationListeners.add(ActivationChangedEvents(priority, listener))
        activationListeners.sortByDescending { it.priority }

        // Everything before this index runs before the value is written, everything from
        // it onwards runs after. With no negative priority listener the split sits past
        // the end of the list, so every listener runs before the write.
        val firstNegativePriority = activationListeners.indexOfFirst { it.priority < 0 }
        zeroPriorityIndex = if (firstNegativePriority == -1) {
            activationListeners.size
        } else firstNegativePriority
    }

    operator fun getValue(thisRef: ActiveState, property: KProperty<*>): Boolean {
        return active
    }

    operator fun setValue(thisRef: ActiveState, property: KProperty<*>, value: Boolean) {
        val hasChanged = active != value
        if (hasChanged) {
            runHighPriorityCallbacks(value)

            active = value
            if (value) thisRef.onActivate()
            else thisRef.onDeactivate()

            runLowPriorityCallbacks(value)
        }
    }

    private fun runHighPriorityCallbacks(activate: Boolean) {
        for (i in 0 until zeroPriorityIndex) {
            activationListeners[i].listener.invoke(activate)
        }
    }

    private fun runLowPriorityCallbacks(activate: Boolean) {
        for (i in zeroPriorityIndex until activationListeners.size) {
            activationListeners[i].listener.invoke(activate)
        }
    }

    private class ActivationChangedEvents(val priority: Int, val listener: (Boolean) -> Unit)
}
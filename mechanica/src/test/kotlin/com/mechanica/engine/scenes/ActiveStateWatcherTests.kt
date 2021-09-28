package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.activation.ActivationListener
import com.mechanica.engine.scenes.activation.ActiveStateWatcher
import org.junit.Test
import kotlin.test.assertEquals

class ActiveStateWatcherTests {
    @Test
    fun activeStateWatcherCallsCallbacks() {
        val watcher = ActiveStateWatcherImpl()

        watcher.active = false

        assertEquals(1, watcher.callbackCounter, "onDeactivate was not called when deactivating the ActiveStateWatcher")

        watcher.active = false

        assertEquals(1, watcher.callbackCounter, "onDeactivate was called even though the ActiveStateWatcher was already deactivated")

        watcher.active = true

        assertEquals(2, watcher.callbackCounter, "onActivate was not called when deactivating the ActiveStateWatcher")

        watcher.active = true

        assertEquals(2, watcher.callbackCounter, "onActivate was called even though the ActiveStateWatcher was already deactivated")
    }

    @Test
    fun activeStateWatcherCallsListeners() {
        var listenerCallCount = 0
        val watcher = ActiveStateWatcherImpl()

        watcher.addActiveStateChangedListener { listenerCallCount++ }

        assertEquals(0, listenerCallCount, "ActiveStateChangedListener was called even though the active state was not changed")

        watcher.active = false

        assertEquals(1, listenerCallCount, "ActiveStateChangedListener was not called even though the active state was changed")

    }

    @Test
    fun activeStateWatcherCallsListenersInPriorityOrder() {
        val watcher = ActiveStateWatcherImpl()

        fun assertLowPriority(value: Boolean) = assert(value == watcher.active) { "The listener has a negative priority but the active state was already changed" }
        fun assertHighPriority(value: Boolean) = assert(value != watcher.active) { "The listener has a positive priority but the active state was not changed" }

        watcher.addActiveStateChangedListener(-1, ::assertLowPriority)
        watcher.addActiveStateChangedListener(3, ::assertHighPriority)
        watcher.addActiveStateChangedListener(-2, ::assertLowPriority)
        watcher.addActiveStateChangedListener(4, ::assertHighPriority)

        watcher.active = false
        watcher.active = false
        watcher.active = true
    }

}

class ActiveStateWatcherImpl(
    private val onActivateCallback: () -> Unit = {},
    private val onDeactivateCallback: () -> Unit = {}) : ActiveStateWatcher {

    override val activator = ActivationListener()
    var activateCounter = 0
        private set
    var deactivateCounter = 0
        private set
    val callbackCounter: Int
        get() = activateCounter + deactivateCounter

    override fun onActivate() {
        activateCounter++
        onActivateCallback()
    }

    override fun onDeactivate() {
        deactivateCounter++
        onDeactivateCallback()
    }
}
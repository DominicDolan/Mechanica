package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.activation.ActivationListener
import com.mechanica.engine.scenes.activation.ActiveState
import org.junit.Test
import kotlin.test.assertEquals

class ActiveStateTests {
    @Test
    fun activeStateWatcherCallsCallbacks() {
        val watcher = ActiveStateImpl()

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
        val watcher = ActiveStateImpl()

        watcher.addActiveStateChangedListener { listenerCallCount++ }

        assertEquals(0, listenerCallCount, "ActiveStateChangedListener was called even though the active state was not changed")

        watcher.active = false

        assertEquals(1, listenerCallCount, "ActiveStateChangedListener was not called even though the active state was changed")

    }

    @Test
    fun listenersRunBeforeTheValueChanges() {
        val watcher = ActiveStateImpl()
        val observed = ArrayList<Boolean>()

        watcher.addActiveStateChangedListener { observed.add(watcher.active) }
        watcher.addActiveStateChangedListener { observed.add(watcher.active) }

        watcher.active = false

        assertEquals(listOf(true, true), observed,
            "a listener should observe the state being left behind, so it must run before the write")
    }

    @Test
    fun listenersRunInRegistrationOrder() {
        val watcher = ActiveStateImpl()
        val order = ArrayList<Int>()

        watcher.addActiveStateChangedListener { order.add(1) }
        watcher.addActiveStateChangedListener { order.add(2) }
        watcher.addActiveStateChangedListener { order.add(3) }

        watcher.active = false

        assertEquals(listOf(1, 2, 3), order, "listeners should run in the order they were registered")
    }

    @Test
    fun listenersRunBeforeTheActivationCallbacks() {
        val order = ArrayList<String>()
        val watcher = ActiveStateImpl(
            onDeactivateCallback = { order.add("onDeactivate") })

        watcher.addActiveStateChangedListener { order.add("listener") }

        watcher.active = false

        assertEquals(listOf("listener", "onDeactivate"), order,
            "listeners should run before onActivate/onDeactivate")
    }

}

class ActiveStateImpl(
    private val onActivateCallback: () -> Unit = {},
    private val onDeactivateCallback: () -> Unit = {}) : ActiveState {

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
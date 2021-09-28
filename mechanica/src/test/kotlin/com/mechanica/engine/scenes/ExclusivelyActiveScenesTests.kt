package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.activation.ActiveState
import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivation
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExclusivelyActiveScenesTests {

    lateinit var watchers: Array<ActiveState>
    @Before
    fun setUp() {
        watchers = Array(4) { ActiveStateImpl() }
    }

    @Test
    fun oneWatcherIsActiveWhenSetToExclusive() {
        ExclusiveActivation.exactlyOneActivation(*watchers)

        assertOneIsActive()

        watchers[1].active = true

        assertOneIsActive()

        watchers[1].active = false

        assertOneIsActive()

        watchers[3].active = true

        assertOneIsActive()

        watchers[3].active = false

        assertOneIsActive()

    }

    @Test
    fun oneOrNoWatchersAreActiveWhenSetToNoneOrOne() {
        ExclusiveActivation.oneOrNoneActivation(*watchers)

        assertOneIsActive()

        watchers[1].active = true

        assertOneIsActive()

        watchers[1].active = false

        assertNoneIsActive()

        watchers[3].active = true

        assertOneIsActive()

        watchers[3].active = false

        assertNoneIsActive()
    }

    @Test
    fun exclusivelyActivationGetsTheRightWatcher() {
        val exclusiveActivation = ExclusiveActivation.exactlyOneActivation(*watchers)

        assertWatcherByIndexIsActive(exclusiveActivation, 0)

        watchers[1].active = true

        assertWatcherByIndexIsActive(exclusiveActivation, 1)

        watchers[1].active = false

        assertWatcherByIndexIsActive(exclusiveActivation, 0)

        watchers[3].active = true

        assertWatcherByIndexIsActive(exclusiveActivation, 3)

        watchers[3].active = false

        assertWatcherByIndexIsActive(exclusiveActivation, 0)
    }

    @Test
    fun oneOrNoneActivationGetsTheRightWatcher() {
        val exclusiveActivation = ExclusiveActivation.oneOrNoneActivation(*watchers)

        assertWatcherByIndexIsActive(exclusiveActivation, 0)

        watchers[1].active = true

        assertWatcherByIndexIsActive(exclusiveActivation, 1)

        watchers[1].active = false

        assertWatcherByIndexIsActive(exclusiveActivation, -1)

        watchers[3].active = true

        assertWatcherByIndexIsActive(exclusiveActivation, 3)

        watchers[3].active = false

        assertWatcherByIndexIsActive(exclusiveActivation, -1)
    }

    private fun assertOneIsActive() {
        val activeCount = watchers.count { it.active }
        assertEquals(1, activeCount, "Only one watcher should be active but $activeCount watchers are active")
    }

    private fun assertNoneIsActive() {
        val activeCount = watchers.count { it.active }
        assertEquals(0, activeCount, "No watcher should be active but $activeCount watchers are active")
    }

    private fun assertWatcherByIndexIsActive(activation: ExclusiveActivation<*>, index: Int) {
        val activeWatcher = watchers.getOrNull(index)
        assertEquals(activation.active, activeWatcher, "exclusiveActivation.active should refer to the currently active watcher")
    }
}
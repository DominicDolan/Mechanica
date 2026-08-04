package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.states.SceneState
import com.mechanica.engine.scenes.states.SceneStateMachine
import com.mechanica.engine.scenes.states.sceneStateMachine
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SceneStateMachineTests {

    private class TestState(val name: String) : SceneState {
        override var active = false

        var enterCount = 0
            private set
        var exitCount = 0
            private set
        var enteredFrom: SceneState? = null
            private set
        var exitedTo: SceneState? = null
            private set

        /** What [SceneStateMachine.current] pointed at while the hook was running. */
        var currentDuringEnter: SceneState? = null
        var currentDuringExit: SceneState? = null

        var machine: SceneStateMachine<TestState>? = null

        override fun onEnter(from: SceneState?) {
            enterCount++
            enteredFrom = from
            currentDuringEnter = machine?.current
        }

        override fun onExit(to: SceneState?) {
            exitCount++
            exitedTo = to
            currentDuringExit = machine?.current
        }

        override fun toString() = name
    }

    private fun machineOf(
        default: TestState,
        vararg states: Pair<TestState, () -> Boolean>
    ): SceneStateMachine<TestState> {
        val machine = sceneStateMachine(default) {
            for ((state, guard) in states) state(state, guard)
        }
        default.machine = machine
        for ((state, _) in states) state.machine = machine
        return machine
    }

    @Test
    fun theDefaultStateIsCurrentAndActiveOnConstruction() {
        val default = TestState("default")
        val other = TestState("other")

        val machine = machineOf(default, other to { false })

        assertSame(default, machine.current, "the default state should be current")
        assertTrue(default.active, "the default state should be active")
        assertEquals(false, other.active, "a non current state should not be active")
    }

    @Test
    fun theFirstMatchingGuardWins() {
        val default = TestState("default")
        val first = TestState("first")
        val second = TestState("second")

        val machine = machineOf(default, first to { true }, second to { true })
        machine.evaluate()

        assertSame(first, machine.current, "the earliest declared matching guard should win")
    }

    @Test
    fun theMachineFallsBackToTheDefaultWhenNoGuardHolds() {
        val default = TestState("default")
        val other = TestState("other")
        var otherApplies = true

        val machine = machineOf(default, other to { otherApplies })

        machine.evaluate()
        assertSame(other, machine.current, "the matching state should become current")

        otherApplies = false
        machine.evaluate()
        assertSame(default, machine.current, "the machine should fall back to the default")
    }

    @Test
    fun exactlyOneStateIsActiveAfterEveryTransition() {
        val default = TestState("default")
        val a = TestState("a")
        val b = TestState("b")
        var pick = ""

        val machine = machineOf(default, a to { pick == "a" }, b to { pick == "b" })
        val all = listOf(default, a, b)

        for (choice in listOf("a", "b", "", "b", "a")) {
            pick = choice
            machine.evaluate()
            assertEquals(1, all.count { it.active }, "exactly one state should be active after evaluating for '$choice'")
            assertTrue(machine.current.active, "the current state should be the active one")
        }
    }

    @Test
    fun transitionHooksReceiveTheirCounterpart() {
        val default = TestState("default")
        val other = TestState("other")
        var otherApplies = true

        val machine = machineOf(default, other to { otherApplies })

        machine.evaluate()

        assertEquals(1, default.exitCount, "the outgoing state should be exited once")
        assertEquals(1, other.enterCount, "the incoming state should be entered once")
        assertSame(other, default.exitedTo, "onExit should receive the incoming state")
        assertSame(default, other.enteredFrom, "onEnter should receive the outgoing state")

        otherApplies = false
        machine.evaluate()

        assertSame(default, other.exitedTo, "onExit should receive the incoming state on the way back")
        assertSame(other, default.enteredFrom, "onEnter should receive the outgoing state on the way back")
    }

    @Test
    fun eachHookRunsWhileItsOwnStateIsCurrent() {
        val default = TestState("default")
        val other = TestState("other")

        val machine = machineOf(default, other to { true })
        machine.evaluate()

        assertSame(default, default.currentDuringExit, "onExit should run while the exiting state is still current")
        assertSame(other, other.currentDuringEnter, "onEnter should run once the entering state is current")
    }

    @Test
    fun reEvaluatingTheSameStateDoesNotRefireTheHooks() {
        val default = TestState("default")
        val other = TestState("other")

        val machine = machineOf(default, other to { true })

        machine.evaluate()
        machine.evaluate()
        machine.evaluate()

        assertEquals(1, other.enterCount, "onEnter should not fire again while the state stays current")
        assertEquals(1, default.exitCount, "onExit should not fire again while the state stays inactive")
    }

    @Test
    fun theFirstEntryReportsNoPreviousStateOnlyWhenThereIsNone() {
        val default = TestState("default")
        val other = TestState("other")

        val machine = machineOf(default, other to { true })

        assertNull(other.enteredFrom, "a state that has never been entered has no predecessor")

        machine.evaluate()

        assertSame(default, other.enteredFrom, "the default state is the predecessor of the first transition")
    }

    @Test
    fun transitionToBypassesTheGuards() {
        val default = TestState("default")
        val other = TestState("other")

        val machine = machineOf(default, other to { false })

        machine.transitionTo(other)

        assertSame(other, machine.current, "transitionTo should ignore the guards")
        assertTrue(other.active, "the target of transitionTo should be active")
    }
}

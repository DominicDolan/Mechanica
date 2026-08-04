package com.mechanica.engine.scenes.states

import com.mechanica.engine.scenes.scenes.SceneNode

/**
 * A [SceneNode] whose activity is owned by a [SceneStateMachine].
 *
 * Exactly one state of a machine is current at a time, and the machine is the only thing
 * that writes [active]. A state therefore never has to ask to be activated: the condition
 * under which it should run is declared on the machine, next to the state itself.
 */
interface SceneState : SceneNode {

    override var active: Boolean

    /**
     * Called once this state has become the machine's current state.
     *
     * [from] is the state that was current immediately before, or null if the machine had
     * no previous state. Anything this state needs to carry over from the outgoing state -
     * a position, a velocity, an animation pose - should be read from [from] rather than
     * from a shared parent, because the parent already reports this state as current by the
     * time this runs.
     */
    fun onEnter(from: SceneState?) {}

    /**
     * Called while this state is still the machine's current state, immediately before
     * [to] takes over.
     */
    fun onExit(to: SceneState?) {}
}

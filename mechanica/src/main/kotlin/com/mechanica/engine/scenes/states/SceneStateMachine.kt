package com.mechanica.engine.scenes.states

/**
 * Decides which one of a set of [SceneState]s should be active.
 *
 * Each state is declared together with the guard that selects it. [evaluate] walks the
 * guards in declaration order and takes the first that holds, falling back to the default
 * state when none do. Because the machine owns [SceneState.active], a state never sets a
 * flag asking to be run and no other object has to poll for one.
 *
 * A transition delivers exactly one [SceneState.onExit] to the outgoing state and one
 * [SceneState.onEnter] to the incoming state, and each hook runs while its own state is
 * [current]. The counterpart is passed in, so a state that has to inherit something from
 * its predecessor gets it directly rather than having to reach for it before some other
 * listener overwrites it.
 *
 * Build one with [sceneStateMachine].
 */
class SceneStateMachine<S : SceneState> internal constructor(
    private val default: S,
    private val transitions: List<Transition<S>>
) {

    /** The state that is currently active. Never null. */
    var current: S = default
        private set

    init {
        for (i in transitions.indices) {
            transitions[i].state.active = false
        }
        default.active = true
    }

    /**
     * Re-evaluates the guards and transitions if a different state now applies.
     * Typically called once per frame from the owning scene's update.
     */
    fun evaluate() {
        for (i in transitions.indices) {
            val transition = transitions[i]
            if (transition.guard()) {
                transitionTo(transition.state)
                return
            }
        }
        transitionTo(default)
    }

    /**
     * Transitions to [next] directly, ignoring the guards. Does nothing if [next] is
     * already [current], so the hooks never fire twice for the same state.
     */
    fun transitionTo(next: S) {
        if (next === current) return

        val previous = current
        previous.onExit(next)

        previous.active = false
        current = next
        next.active = true

        next.onEnter(previous)
    }

    internal class Transition<S : SceneState>(val state: S, val guard: () -> Boolean)

    class Builder<S : SceneState> internal constructor(private val default: S) {
        private val transitions = ArrayList<Transition<S>>()

        /**
         * Declares that [state] becomes current whenever [guard] holds and no guard
         * declared before it holds.
         */
        fun state(state: S, guard: () -> Boolean) {
            transitions.add(Transition(state, guard))
        }

        internal fun build() = SceneStateMachine(default, transitions)
    }
}

/**
 * Creates a [SceneStateMachine] that falls back to [default] whenever none of the guards
 * declared in [build] hold.
 *
 * ```
 * val sprites = sceneStateMachine(runningSprite) {
 *     state(deadSprite) { isDead }
 *     state(slidingSprite) { isSliding }
 * }
 * ```
 */
fun <S : SceneState> sceneStateMachine(
    default: S,
    build: SceneStateMachine.Builder<S>.() -> Unit
): SceneStateMachine<S> = SceneStateMachine.Builder(default).apply(build).build()

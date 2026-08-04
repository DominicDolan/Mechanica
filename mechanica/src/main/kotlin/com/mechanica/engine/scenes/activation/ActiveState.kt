package com.mechanica.engine.scenes.activation

interface ActiveState : Activatable {

    val activator: ActivationListener
    override var active: Boolean
        get() = activator.getValue(this, this::active)
        set(value) = activator.setValue(this, this::active, value)


    /**
     * Function to be overriden that will be called immediately after [active] has been set to true
     */
    fun onActivate()

    /**
     * Function to be overriden that will be called immediately after [active] has been set to false
     */
    fun onDeactivate()

    /**
     * Adds a callback for when the value of [active] changes.
     *
     * Callbacks run in the order they were registered, before [active] is written and before
     * [onActivate] or [onDeactivate] is called, so a callback still observes the state that is
     * being left behind. To order work around a transition rather than merely observe it, use a
     * [com.mechanica.engine.scenes.states.SceneStateMachine], whose entry hook is handed the
     * outgoing state directly.
     *
     * @param listener the callback which will execute when the value of [active] has been changed, the lambda takes
     * a boolean expression which is the new value for [active]
     */
    fun addActiveStateChangedListener(listener: (Boolean) -> Unit) {
        activator.addListener(listener)
    }

    companion object {
        fun createDefault(): ActiveState {
            return object : ActiveState {
                override val activator = ActivationListener()
                override fun onActivate() {}
                override fun onDeactivate() {}
            }
        }
    }
}
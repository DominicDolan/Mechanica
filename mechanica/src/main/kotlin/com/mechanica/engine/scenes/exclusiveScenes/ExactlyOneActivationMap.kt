package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveState

open class ExactlyOneActivationMap<P : ActiveState>(vararg states: P) : NoneOrOneActivationMap<P>(*states) {
    private val default: P = states.firstOrNull() ?: throw IllegalArgumentException("At least one scene has to be passed in for exclusive activation")
    override val active: P
        get() = super.active ?: default

    override fun initializeList(states: Array<out P>) {
        super.initializeList(states)

        if (states.isNotEmpty()) {
            setExclusiveActivation(states.first())
        }
    }

    override fun setCurrentlyActiveToInactive(state: P) {
        default.active = true
        setExclusiveActivation(default)
    }
}
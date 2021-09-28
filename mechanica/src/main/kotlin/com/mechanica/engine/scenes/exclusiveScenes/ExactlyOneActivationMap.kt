package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveStateWatcher

open class ExactlyOneActivationMap<P : ActiveStateWatcher>(vararg watchers: P) : NoneOrOneActivationMap<P>(*watchers) {
    private val default: P = watchers.firstOrNull() ?: throw IllegalArgumentException("At least one scene has to be passed in for exclusive activation")
    override val active: P
        get() = super.active ?: default

    override fun initializeList(watchers: Array<out P>) {
        super.initializeList(watchers)

        if (watchers.isNotEmpty()) {
            setExclusiveActivation(watchers.first())
        }
    }

    override fun setCurrentlyActiveToInactive(process: P) {
        default.active = true
        setExclusiveActivation(default)
    }
}
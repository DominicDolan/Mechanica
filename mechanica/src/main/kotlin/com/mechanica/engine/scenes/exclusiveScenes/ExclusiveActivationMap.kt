package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.scenes.SceneNode
import kotlin.reflect.KProperty

open class ExclusiveActivationMap<P : SceneNode>(vararg processes: P) : NoneOrOneActivationMap<P>(*processes) {
    private val default: P = processes.firstOrNull() ?: throw IllegalArgumentException("At least one scene has to be passed in for exclusive activation")
    override val active: P
        get() = super.active ?: default

    override operator fun getValue(thisRef: SceneNode, property: KProperty<*>): P = active

    override fun setCurrentlyActiveToInactive(process: P) {
        default.active = true
        setExclusiveActivation(default)
    }
}
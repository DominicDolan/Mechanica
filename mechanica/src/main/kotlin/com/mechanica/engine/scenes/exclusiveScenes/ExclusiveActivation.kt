package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveStateWatcher
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ExclusiveActivation<P : ActiveStateWatcher> : ReadOnlyProperty<Any?, P?> {
    val active: P?
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): P? = active
    fun <R : P> add(watcher: R): R

    companion object {
        fun <P : ActiveStateWatcher> exactlyOneActivation(vararg watchers: P): ExclusiveActivation<P> {
            return ExactlyOneActivationMap(*watchers)
        }

        fun <P : ActiveStateWatcher> oneOrNoneActivation(vararg watchers: P): ExclusiveActivation<P> {
            return NoneOrOneActivationMap(*watchers)
        }
    }
}

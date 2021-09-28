package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveState
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ExclusiveActivation<P : ActiveState> : ReadOnlyProperty<Any?, P?> {
    val active: P?
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): P? = active
    fun <R : P> add(state: R): R

    companion object {
        fun <P : ActiveState> exactlyOneActivation(vararg states: P): ExclusiveActivation<P> {
            return ExactlyOneActivationMap(*states)
        }

        fun <P : ActiveState> oneOrNoneActivation(vararg states: P): ExclusiveActivation<P> {
            return NoneOrOneActivationMap(*states)
        }
    }
}

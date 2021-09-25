package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.SceneNode
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class NoneOrOneActivationMap<P : SceneNode>(vararg scenes: P) : ReadOnlyProperty<SceneNode, P?> {
    private val scenes: ArrayList<P> = ArrayList()
    private var activeIndex = -1
    open val active: P?
        get() = if (activeIndex != -1) scenes[activeIndex] else null

    init {
        for (i in scenes.indices) {
            add(scenes[i])
        }
    }

    override operator fun getValue(thisRef: SceneNode, property: KProperty<*>): P? = active

    fun add(process: P) = addProcess(process)

    open fun <R : P> addProcess(process: R): R {
        scenes.add(process)
        process.addActivationCallback()
        return process
    }

    fun activateNext() {
        if (activeIndex < scenes.size - 1 && activeIndex >= 0) {
            activeIndex++
        } else {
            activeIndex = 0
        }
        scenes[activeIndex].active = true
    }

    private fun P.addActivationCallback() {
        if (this is SceneHub) {
            addActivationChangedListener(0) {
                if (it) {
                    setExclusiveActivation(this)
                } else if (this@NoneOrOneActivationMap.active === this) {
                    setCurrentlyActiveToInactive(this)
                }
            }
        }
    }

    protected fun setExclusiveActivation(process: P) {
        activeIndex = scenes.indexOf(process)
        for (i in scenes.indices) {
            if (scenes[i] != process) {
                scenes[i].active = false
            }
        }
    }

    protected open fun setCurrentlyActiveToInactive(process: P) {
        activeIndex = -1
    }
}
package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.processes.Process
import com.mechanica.engine.scenes.processes.ProcessNode
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class NoneOrOneActivationMap<P : Process>(vararg processes: P) : ReadOnlyProperty<ProcessNode, P?> {
    private val processes: ArrayList<P> = ArrayList()
    private var activeIndex = -1
    open val active: P?
        get() = if (activeIndex != -1) processes[activeIndex] else null

    init {
        for (i in processes.indices) {
            add(processes[i])
        }
    }

    override operator fun getValue(thisRef: ProcessNode, property: KProperty<*>): P? = active

    fun add(process: P) = addProcess(process)

    open fun <R : P> addProcess(process: R): R {
        processes.add(process)
        process.addActivationCallback()
        return process
    }

    fun activateNext() {
        if (activeIndex < processes.size - 1 && activeIndex >= 0) {
            activeIndex++
        } else {
            activeIndex = 0
        }
        processes[activeIndex].active = true
    }

    private fun P.addActivationCallback() {
        addActivationChangedListener(0) {
            if (it) {
                setExclusiveActivation(this)
            } else if (this@NoneOrOneActivationMap.active === this) {
                setCurrentlyActiveToInactive(this)
            }
        }
    }

    protected fun setExclusiveActivation(process: P) {
        activeIndex = processes.indexOf(process)
        for (i in processes.indices) {
            if (processes[i] != process) {
                processes[i].active = false
            }
        }
    }

    protected open fun setCurrentlyActiveToInactive(process: P) {
        activeIndex = -1
    }
}
package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveState

open class NoneOrOneActivationMap<P : ActiveState>(vararg states: P) : ExclusiveActivation<P> {
    private val states: ArrayList<P> = ArrayList()
    private var activeIndex = -1
    override val active: P?
        get() = if (activeIndex != -1) states[activeIndex] else null

    init {
        for (i in states.indices) {
            add(states[i])
        }


        @Suppress("LeakingThis")// Leaking this shouldn't be a problem because this is the last method in the constructor
        initializeList(states)
    }

    protected open fun initializeList(states: Array<out P>) {
        for (i in states.indices) {
            if(states[i].active) {
                setExclusiveActivation(states[i])
                return
            }
        }
    }

    final override fun <R : P> add(state: R): R {
        states.add(state)
        state.addActivationCallback()
        return state
    }

    fun activateNext() {
        if (activeIndex < states.size - 1 && activeIndex >= 0) {
            activeIndex++
        } else {
            activeIndex = 0
        }
        states[activeIndex].active = true
    }

    private fun P.addActivationCallback() {
        addActiveStateChangedListener(0) {
            if (it) {
                setExclusiveActivation(this)
            } else if (this@NoneOrOneActivationMap.active === this) {
                setCurrentlyActiveToInactive(this)
            }
        }
    }

    protected fun setExclusiveActivation(state: P) {
        activeIndex = states.indexOf(state)
        for (i in states.indices) {
            if (states[i] != state) {
                states[i].active = false
            }
        }
    }

    protected open fun setCurrentlyActiveToInactive(state: P) {
        activeIndex = -1
    }
}
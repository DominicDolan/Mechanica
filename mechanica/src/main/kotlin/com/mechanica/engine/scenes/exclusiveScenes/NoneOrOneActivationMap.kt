package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.scenes.activation.ActiveStateWatcher

open class NoneOrOneActivationMap<P : ActiveStateWatcher>(vararg watchers: P) : ExclusiveActivation<P> {
    private val watchers: ArrayList<P> = ArrayList()
    private var activeIndex = -1
    override val active: P?
        get() = if (activeIndex != -1) watchers[activeIndex] else null

    init {
        for (i in watchers.indices) {
            add(watchers[i])
        }


        @Suppress("LeakingThis")// Leaking this shouldn't be a problem because this is the last method in the constructor
        initializeList(watchers)
    }

    protected open fun initializeList(watchers: Array<out P>) {
        for (i in watchers.indices) {
            if(watchers[i].active) {
                setExclusiveActivation(watchers[i])
                return
            }
        }
    }

    final override fun <R : P> add(watcher: R): R {
        watchers.add(watcher)
        watcher.addActivationCallback()
        return watcher
    }

    fun activateNext() {
        if (activeIndex < watchers.size - 1 && activeIndex >= 0) {
            activeIndex++
        } else {
            activeIndex = 0
        }
        watchers[activeIndex].active = true
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

    protected fun setExclusiveActivation(watcher: P) {
        activeIndex = watchers.indexOf(watcher)
        for (i in watchers.indices) {
            if (watchers[i] != watcher) {
                watchers[i].active = false
            }
        }
    }

    protected open fun setCurrentlyActiveToInactive(process: P) {
        activeIndex = -1
    }
}
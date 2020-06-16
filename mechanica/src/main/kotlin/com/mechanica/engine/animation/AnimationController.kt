package com.mechanica.engine.animation

import com.mechanica.engine.scenes.processes.Updateable

interface AnimationController : Updateable {

    val startTime: Double
    val endTime: Double

    val time: Double

    var looped: Boolean
    var paused: Boolean

    val isPlaying: Boolean
        get() = !paused

    fun goTo(time: Double)

    fun restart() {
        goTo(startTime)
        update(0.0)
    }

    fun pause() {
        paused = true
    }

    fun play() {
        paused = false
        if (time == endTime) {
            restart()
        }
    }
}
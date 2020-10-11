package com.mechanica.engine.animation

import com.mechanica.engine.scenes.processes.Updateable

interface AnimationController : Updateable {

    val startTime: Double
    val endTime: Double
    val length: Double
        get() = endTime - startTime

    val time: Double

    var looped: Boolean
    var paused: Boolean

    val isPlaying: Boolean
        get() = !paused

    fun goTo(time: Double)

    fun restart() {
        goTo(startTime)
        val pauseState = paused
        paused = false
        update(0.0)
        paused = pauseState
    }

    fun pause() {
        paused = true
    }

    fun play() {
        paused = false
    }

    fun playFromStart() {
        restart()
        play()
    }
}
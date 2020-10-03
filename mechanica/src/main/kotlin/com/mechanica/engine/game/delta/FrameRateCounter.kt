package com.mechanica.engine.game.delta

import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.util.Timer

class FrameRateCounter {

    var frameCounter = 0
        private set
    private var lastHalfSecondCount = 30

    private var timerStart = Timer.now

    fun update() {
        frameCounter++
        if (Timer.now - timerStart > 0.5) {
            timerStart = Timer.now
            lastHalfSecondCount = frameCounter
            frameCounter = 0
        }
    }

    fun print() {
        println(toString())
    }

    fun screenLog() {
        ScreenLog { toString() }
    }

    override fun toString(): String {
        return "fps: ${2*lastHalfSecondCount}"
    }
}
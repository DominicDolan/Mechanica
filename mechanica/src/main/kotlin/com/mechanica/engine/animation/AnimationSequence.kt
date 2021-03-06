package com.mechanica.engine.animation

import com.mechanica.engine.util.extensions.constrainLooped
import com.mechanica.engine.util.extensions.fori

class AnimationSequence(private vararg val animations: AnimationController): AnimationController {
    override val startTime: Double
    override val endTime: Double

    override var looped: Boolean = false

    override var time: Double = 0.0
        private set(value) {
            field = if (looped) {
                value.constrainLooped(startTime, endTime)
            } else {
                value
            }
        }

    override var paused = false
        set(value) {
            animations.fori {
                it.paused = value
            }
            field = value
        }

    init {
        var startTime = 0.0
        var endTime = 0.0
        for (a in animations) {
            if (a.startTime < startTime) {
                startTime = a.startTime
            }
            if (a.endTime > endTime) {
                endTime = a.endTime
            }
        }
        this.startTime = startTime
        this.endTime = endTime

        time = startTime
    }

    override fun pause() {
        super.pause()
        animations.fori {
            it.pause()
        }
    }

    override fun play() {
        super.play()
        animations.fori {
            it.paused = false
        }
    }

    override fun goTo(time: Double) {
        this.time = time
        animations.fori {
            it.goTo(time)
        }
    }

    override fun update(delta: Double) {
        if (!paused) {
            animations.fori {
                it.goTo(time)
            }
            time += delta
        }
    }

}
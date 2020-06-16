package com.mechanica.engine.animation

import com.mechanica.engine.models.Image
import com.mechanica.engine.resources.ResourceDirectory
import com.mechanica.engine.util.extensions.constrain
import com.mechanica.engine.util.extensions.constrainLooped
import com.mechanica.engine.utils.loadImage
import kotlin.math.floor
import kotlin.math.sign

class FrameAnimation(private val frames: List<Image>, frameRate : Double,
                     override var startTime: Double = 0.0,
                     override var endTime: Double = frames.size.toDouble()/frameRate): AnimationController {
    val duration: Double
        get() = endTime - startTime
    private val direction = sign(duration)
    private val originalDuration = duration

    override var paused = false

    override var time: Double = startTime
        private set(value) {
            field = if (looped) {
                value.constrainLooped(startTime, endTime)
            } else {
                value.constrain(startTime, endTime)
            }
        }
    private val relativeTime: Double
        get() = time - startTime

    private val fraction: Double
        get() = relativeTime/duration

    val currentFrame: Image
        get() {
            val index = floor(fraction*(frames.size-1)).toInt()
            return frames[index]
        }

    var scale: Double
        get() = duration/originalDuration
        set(value) {
            val scaleValue = direction/value
            endTime = startTime + scaleValue*originalDuration
            time = startTime + scaleValue*fraction*originalDuration
        }

    override var looped: Boolean = true

    override fun goTo(time: Double) {
        this.time = time
    }

    override fun restart() {
        time = startTime
    }

    override fun pause() {
        paused = true
    }

    override fun play() {
        paused = false
        if (time == endTime) {
            time = startTime
        }
    }

    override fun update(delta: Double) {
        time += direction*delta
    }

    companion object {
        fun loadAnimation(directory: ResourceDirectory, frameRate: Double = 24.0)
                = FrameAnimation(directory.map { loadImage(it) }, frameRate)
    }
}
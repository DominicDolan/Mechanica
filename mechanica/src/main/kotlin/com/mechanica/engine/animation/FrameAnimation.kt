package com.mechanica.engine.animation

import com.mechanica.engine.models.Image
import com.mechanica.engine.resources.ResourceDirectory
import com.mechanica.engine.utils.loadImage
import kotlin.math.abs
import kotlin.math.floor

class FrameAnimation(private val frames: List<Image>, frameRate : Double) {
    val duration = frames.size.toDouble()/frameRate
    private var progress = 0.0
        set(value) { field = value%duration }

    var fraction: Double
        get() = progress/duration
        set(value) {
            progress = value*duration
        }

    var currentFrame: Image
        get() {
            val index = floor(fraction*frames.size).toInt()
            return frames[index]
        }
        set(value) {
            fraction = value.id.toDouble()/frames.size.toDouble()
        }

    var scale: Double = 1.0
        set(value) {
            field = abs(value)
        }

    fun play(delta: Double, reversed: Boolean = false) {
        if (!reversed) {
            progress += scale*delta
        } else {
            progress -= scale*delta
        }
    }

    companion object {
        fun loadAnimation(directory: ResourceDirectory, frameRate: Double = 24.0)
                = FrameAnimation(directory.map { loadImage(it) }, frameRate)
    }
}
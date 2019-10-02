package animation

import kotlin.math.abs
import kotlin.math.floor

class FrameAnimation(private val frames: List<Int>, frameRate : Double) {
    val duration = frames.size.toDouble()/frameRate
    private var progress = 0.0
        set(value) { field = value%duration }

    var fraction: Double
        get() = progress/duration
        set(value) {
            progress = value*duration
        }

    var currentFrame: Int
        get() {
            val index = floor(fraction*frames.size).toInt()
            return frames[index]
        }
        set(value) {
            fraction = value.toDouble()/frames.size.toDouble()
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
}
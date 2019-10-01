package animation

import kotlin.math.floor

class FrameAnimation(private val frames: List<Int>, frameRate : Double) {
    private val duration = frames.size.toDouble()/frameRate
    private var progress = 0.0
        set(value) { field = value%duration }

    var currentFrame: Int = 0
        private set

    fun play(delta: Float) {
        progress += delta
        val progressFraction = progress/duration
        val index = floor(progressFraction*frames.size).toInt()
        currentFrame = frames[index]
    }
}
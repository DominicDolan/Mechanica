package com.mechanica.engine.samples.polygon

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.plus
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        setStartingScene { LineDrawer() }
    }

    Game.loop()
}


class LineDrawer : Scene() {
    private val center = Vector2.create()
    private val end = MutableVector2.create()
    private var angle = 0.degrees

    override fun update(delta: Double) {
        end.set(vec(center) + vec(3.0, angle))
        angle += (30 * delta).degrees
    }

    override fun render(draw: Drawer) {
        draw.black.stroke(0.05).line(center, end)
    }
}
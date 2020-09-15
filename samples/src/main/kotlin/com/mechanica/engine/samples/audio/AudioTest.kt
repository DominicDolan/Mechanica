package com.mechanica.engine.samples.audio

import com.mechanica.engine.audio.Listener
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.resources.Res

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val source = Res.audio("radar3").createSource()

    Game.loop {
        if (Keyboard.space.hasBeenPressed) {
            source.play()
        }
        if (Keyboard.esc.hasBeenPressed) {
            source.stop()
        }
        if (Keyboard.P.hasBeenPressed) {
            source.pause()
        }
        if (Keyboard.up.hasBeenPressed) {
            source.pitch += 0.1f
        }
        if (Keyboard.down.hasBeenPressed) {
            source.pitch -= 0.1f
        }

        Listener.position.set(Mouse.world.x.toFloat()/5f, 0f, Mouse.world.y.toFloat()/2f)
    }
}
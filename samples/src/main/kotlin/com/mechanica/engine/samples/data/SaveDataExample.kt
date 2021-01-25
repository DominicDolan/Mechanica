package com.mechanica.engine.samples.data

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Key
import com.mechanica.engine.input.KeyIDs
import com.mechanica.engine.persistence.persistent
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.shaders.text.Text

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { SaveDataExample() }
    }

    Game.loop()
}

class SaveDataExample : WorldScene() {
    private var count by persistent(0)

    private val upKey = Key(KeyIDs.W, KeyIDs.UP, KeyIDs.SCROLL_UP)
    private val downKey = Key(KeyIDs.S, KeyIDs.DOWN, KeyIDs.SCROLL_DOWN)

    private val info by lazy { Text("Press $upKey or $downKey to change count\nThe value persists after re-running") }

    override fun render(draw: Drawer) {
        if (upKey.hasBeenPressed) {
            count++
        }
        if (downKey.hasBeenPressed) {
            count--
        }

        draw.centered.darkGrey.text("count: $count", y = 1.0)
        draw.centered.darkGrey.text(info, 0.4, 0.0, -1.0)
    }
}

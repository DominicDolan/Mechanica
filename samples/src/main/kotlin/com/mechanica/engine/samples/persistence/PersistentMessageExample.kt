package com.mechanica.engine.samples.persistence

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.TextInput
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.persistence.persistent
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.shaders.text.Text

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { PersistentMessageExample() }
        setFullscreen(false)
    }

    Game.loop()
}

class PersistentMessageExample : WorldScene() {
    private var runCount by persistent(0)
    private var message by persistent("")
    private var oldMessage = "" + message

    private val runCountInfo by lazy { Text("This example has been run $runCount time${if (runCount != 1) "s" else ""} so far") }
    private val messagePrompt by lazy {
        if (oldMessage.isBlank()) Text("Type a message! It will be saved for the next time this example is run")
        else Text("The last user typed:\n\n\nTry typing something for the next user")
    }
    private val previousMessageText by lazy { Text(oldMessage) }

    init {
        runCount++
        message = ""
    }

    override fun render(draw: Drawer) {
        draw.darkGrey.text(runCountInfo, 0.3, Game.ui.left + 0.1, Game.ui.top - 0.3)
        draw.centered.darkGrey.text(messagePrompt, 0.4, y = 2.5)
        draw.centered.green.text(previousMessageText, 0.5, y = 2.5)

        if (TextInput.hasTextInput) {
            val chars = CharArray(1)
            TextInput.getCodepoints(chars)
            message += chars.joinToString("")
        }

        if (Keyboard.backspace.hasBeenPressed) {
            message = message.substring(0..message.length - 2)
        }

        draw.centered.green.text(message, 0.4, 0.0, 0.0)

        if (Keyboard.esc.hasBeenPressed) {
            Game.close()
        }
    }
}

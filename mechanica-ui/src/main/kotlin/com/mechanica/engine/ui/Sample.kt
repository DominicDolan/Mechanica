package com.mechanica.engine.ui

import com.dubulduke.ui.element.Element
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.MainUIScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { object : MainUIScene() { init {
            addScene(GUI())
        }} }
    }

    Game.run()
}

class GUI : GUIScene() {
    override fun Element<Style, Events>.ui(draw: Drawer) {
        box {
            layout.edit { p, s ->
                top = p.top
                width = 0.5*p.width
                center.x = p.center.x
                height = 5.0
            }
            style {
                color = 0xFF00FFFF
                textColor = 0x00FF00FF
            }

            text("Hello, UI")

            box {
                layout.edit { p, s ->
                    left = p.left
                    center.y = p.center.y
                    width = 1.0
                    height = 1.0
                }
                style { color = 0xFF0000FF }
            }
            box {
                layout.edit { p, s ->
                    width = 1.0
                    height = 2.0
                    left = s.right + 0.5
                    top = s.top
                }
                style { color = 0xFF0000FF }
            }
        }
    }
}
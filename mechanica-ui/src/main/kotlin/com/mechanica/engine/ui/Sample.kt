package com.mechanica.engine.ui

import com.mechanica.engine.context.DesktopApplication
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.MainUIScene
import com.mechanica.engine.ui.elements.*

fun main() {
    Game.configureAs(DesktopApplication()) {
        setViewport(height = 10.0)
        setStartingScene { object : MainUIScene() { init {
            addScene(GUI())
        }} }
        configureDebugMode { screenLog = true }
    }

    Game.run()
}

class GUI : GUIScene() {
    override fun DrawerElement.ui(draw: Drawer) {
        box {
            layout.edit { p, s ->
                top = p.top
                width = 0.5*p.width
                center.x = p.center.x
                height = 5.0
            }
            style {
                color.set(0xFF00FFFF)
                textColor.set(0x00FF00FF)
            }

            text("Hello, UI")

            box {
                layout.edit { p, s ->
                    left = p.left
                    center.y = p.center.y
                    width = 1.0
                    height = 1.0
                }
                style { color.set(0xFF0000FF) }
            }
            box {
                layout.edit { p, s ->
                    width = 1.0
                    bottom = p.bottom - 0.5
                    left = s.right + 0.5
                    top = s.top
                }
                style { color.set(0xFF0000FF) }
            }
            box {
                layout.edit { p, s ->
                    left = s.right
                    right = p.right
                    top = s.top
                    height = 6.0

                    padding(horizontal = 0.5)
                }
                style { color.set(0xFF0000FF) }

                customListItem("list item 1")
                customListItem("list item 2")
                customListItem("list item 3")
                customListItem("list item 4")
            }
        }
    }

    fun DrawerElement.customListItem(label: String) {
        listItem(0.5) {
            style {
                textColor.set(0x40CC60FF)
                fontSize = 0.4
            }
            text(label)
        }
    }
}
package demo.ui

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import state.State
import util.colors.rgba

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { UITest() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class UITest : State() {
    val ui: StartUI

    init {
        ui = StartUI(-6.0, -4.0, 12.0, 8.0)
    }

    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        ui(draw) {
            button(draw) {
                this.color = rgba(0.0, 1.0, 0.0, 0.5)
                this.x = parent.x + 1.0
                this.width = parent.width - 2.0
                button(draw) {
                    this.color = rgba(0.0, 0.0, 1.0, 0.5)
                    this.x = parent.x + 1.0
                    this.width = parent.width - 2.0
                }
            }
        }
    }
}

class Layout {
    var x: Double = 0.0
    var y: Double = 0.0
    var width: Double = 0.0
    var height: Double = 0.0
}

abstract class UIElement(private val startUI: StartUI) {
    val layout = Layout()
    val parent: Layout = Layout()
    var x: Double = 0.0
    var y: Double = 0.0
    var width: Double = 0.0
    var height: Double = 0.0

    operator fun invoke(draw: Drawer, block: StartUI.() -> Unit) {
        startUI.parent.x = startUI.x
        startUI.parent.y = startUI.y
        startUI.parent.width = startUI.width
        startUI.parent.height = startUI.height
        block(startUI)
        draw.color(startUI.color).rectangle(startUI.x, startUI.y, startUI.width, startUI.height)
    }
}

class StartUI(var x: Double, var y: Double, var width: Double, var height: Double){
    val layout = Layout()
    val parent: Layout = Layout()
    val button = ButtonUI(this)
    var color = rgba(1.0, 0.0, 0.0, 0.5)

    init {
        layout.x = x
        layout.y = y
        layout.width = width
        layout.height = height
    }

    private fun resetLayout() {
        x = layout.x
        y = layout.y
        width = layout.width
        height = layout.height
    }

    operator fun invoke(draw: Drawer, block: StartUI.() -> Unit) {
        resetLayout()
        block(this)
    }
}

class ButtonUI(startUI: StartUI) : UIElement(startUI) {

}

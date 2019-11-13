package demo.ui

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import state.State
import util.colors.rgba
import util.extensions.plus
import util.extensions.radians

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
    var angle = 0.radians
    init {
        ui = StartUI(-6.0, -4.0, 12.0, 8.0)
    }

    override fun update(delta: Double) {
        angle += 0.2.radians
    }

    override fun render(draw: Drawer) {

        ui(draw) {
            button {
                this.color = rgba(0.0, 1.0, 0.0, 0.5)
                this.x = parent.x + 3.0
                this.width = parent.width - 6.0
                button {
                    this.color = rgba(0.0, 0.0, 1.0, 0.5)
                    this.x = parent.x + 2.0
                    this.width = parent.width - 4.0
                }
            }
        }

        draw.rotated(angle).rectangle(-8.0, 4.0, 1.0, 1.0)
    }
}

class StartUI(val x: Double, val y: Double, val width: Double, val height: Double) {
    val element = Element()

    init {
        element.x = x
        element.width = width

        element.parent.width = width
        element.parent.x = x
    }

    operator fun invoke(draw: Drawer, block: Element.() -> Unit) {
        block(element)
        draw.color(rgba(0.5,0.5,0.5, 1.0)).rectangle(x, y, width, height)
        render(draw, element)
    }

    private fun render(draw: Drawer, element: Element) {
        element.childCounter = -1
        for (child in element.children) {
            draw.color(child.color).rectangle(child.x, y, child.width, height)
        }
        for (child in element.children) {
            render(draw, child)
        }
    }
}

class Layout {
    var x = 0.0
    var width = 0.0
}

class Element {
    val children = ArrayList<Element>()
    var childCounter = -1
    val parent = Layout()
    var x = 0.0
    var width = 0.0
    var color = rgba(1.0, 0.0, 0.0, 1.0)

    fun childExistsAt(index: Int): Boolean {
        return children.size > index
    }
}

fun Element.button(block: Element.() -> Unit) {
    this.childCounter++
    val newElement = if (childExistsAt(this.childCounter)) {
        this.children.get(this.childCounter)
    } else {
        val e = Element()
        this.children.add(e)
        e
    }

    newElement.parent.x = this.x
    newElement.parent.width = this.width
    block(newElement)

}

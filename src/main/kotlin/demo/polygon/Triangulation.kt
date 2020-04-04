package demo.polygon

import drawer.Drawer
import game.Game
import geometry.triangulation.TriangulatorList
import gl.renderer.PathRenderer
import input.Keyboard
import state.State
import util.colors.hex
import util.extensions.vec

fun main() {
    Game.configure {
        setViewport(height = 1.0)
        setStartingState { TriangulationState() }
        setResolution(1500, 1500)
        configureWindow {
            isDecorated = false
        }
    }
    Game.view.x = Game.view.width/2f
    Game.view.y = Game.view.height/2f

    Game.run {
        if (Keyboard.ESC.hasBeenPressed) {
            Game.close()
        }
    }
}


class TriangulationState: State() {
    val pathRenderer = PathRenderer()
    val path = arrayOf(
            vec(0, 0),
            vec(0.5, 0.1),
            vec(1.0, 0.0),
            vec(0.5, 0.5),
            vec(1.0, 0.7),
            vec(1.0, 1.0),
            vec(0.7, 1.0),
            vec(0.5, 0.7),
            vec(0.0, 1.0),
            vec(0.2, 0.5),
            vec(0.0, 0.0)
    )

    init {
        val linkedList = TriangulatorList(path)


        println("Array:")
        path.contentToString()

        println("\nLinked List")
        println("0: ${linkedList.head}")
        println("1: ${linkedList.head.next}")
        println("2: ${linkedList.head.next.next}")
        println("3: ${linkedList.head.next.next.next}")
        for (v in linkedList) {
            println("v: $v")
//            pathRenderer.path.add(v.point)
        }
    }

    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        draw.yellow.background()
        pathRenderer.stroke = 0.01f
        pathRenderer.color = hex(0x00FF00FF)
        pathRenderer.render()
    }
}
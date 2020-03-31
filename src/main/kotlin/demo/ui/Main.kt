package demo.ui

import drawer.Drawer
import game.Game
import state.State
import util.extensions.plus
import util.extensions.radians

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { UITest() }
    }
    Game.run()
}

private class UITest : State() {
    var angle = 0.radians

    init {

    }

    override fun update(delta: Double) {
        angle += 0.2.radians
    }

    override fun render(draw: Drawer) {
        draw.color(0x990099FF).rectangle(-6, -3.5, 12, 7)

//        ui(draw) {
//            style {
//                color = 0x999999FF
//            }
//            box {
//                style {
//                    color = 0x00FF00FF
//                }
//                layout { parent, _ ->
//                    center.set(parent.center)
//                    width = parent.width - 2.0
//                    height = parent.height - 2.0
//                }
//                box {
//                    style {
//                        color = 0x0000FFFF
//
//                        visualLayout {
//                            y = 0.1
//                        }
//                    }
//                    layout { parent, _ ->
//                        center.x = parent.center.x
//                        width = parent.width - 6.0
//                        top = parent.top
//                        height = parent.height/3.0
//                    }
//                }
//                box {
//                    style {
//                        color = 0x0000FFFF
//
//                    }
//                    layout { parent, previous ->
//                        center.x = parent.center.x
//                        width = parent.width - 6.0
//                        top = previous.bottom - 0.5
//                        height = parent.height/3.0
//                    }
//                }
//            }
//        }

//        draw.rotated(angle).rectangle(-8.0, 4.0, 1.0, 1.0)
    }

}


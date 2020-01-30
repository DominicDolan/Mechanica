package demo.color

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import input.Cursor
import input.Keyboard
import state.State
import util.colors.*
import util.extensions.degrees

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
//            .setFullscreen(true, true)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}


private class StartMain : State() {
    val width = 2
    val height = 1

    val color = hex(0x80FFFFFF)
            //hex(0x521F6DFF)
    var adjusted = hex(0x6D521FFF)
    var blend = 0.0
    var test = hex(0x80FFFFFF)
    var test2 = hsl(180.degrees, 1.0, 0.75)

    init {

        arrayOf(
                0xFFFFFFFF,
                0x808080FF,
                0x000000FF,
                0xFF0000FF,
                0xBFBF00FF,
                0x008000FF,
                0x80FFFFFF,
                0x8080FFFF,
                0xBF40BFFF,
                0xA0A424FF,
                0x411BEAFF,
                0x1EAC41FF,
                0xF0C80EFF,
                0xB430E5FF,
                0xED7651FF,
                0xFEF888FF,
                0x19CB97FF,
                0x362698FF,
                0x7E7EB8FF
        ).forEach { printColor(hex(it)) }
        println()
        println("Test 1:")
        printColor(test)
        println("Test 2:")
        printColor(test2)
    }
    private fun printColor(color: Color) {
        println("$color   ${color.hue}   ${color.saturation}   ${color.lightness}")
    }

    override fun update(delta: Double) {
        adjusted = hsl(color.hue, blend, color.lightness)
        blend = (Cursor.viewX/Game.viewWidth) + 0.5

        if (Keyboard.ESC.hasBeenPressed) {
            Game.close()
        }
    }

    override fun render(draw: Drawer) {
//        var blend = color.linearBlend(blend, adjusted)
        draw.color(adjusted).centered.rectangle(0,0, Game.width, Game.height)

        draw.color(color).centered.rectangle(-width, 0, width, height)
        draw.color(adjusted).centered.rectangle(width, 0, width, height)
    }

}
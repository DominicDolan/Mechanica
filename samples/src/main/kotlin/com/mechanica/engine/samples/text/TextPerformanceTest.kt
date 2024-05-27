package com.mechanica.engine.samples.text

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.Scene


fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { TextPerformanceTest() }
        setFullscreen(false)
        configureDebugMode {
            constructionDraws = true
        }
    }

    Game.loop()
}
const val letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
class TextPerformanceTest : Scene() {
    private val renderer = BasicFontRenderer()
    private var timer = 0.0
    private var index = -1
    private var iterations = 0
    override fun update(delta: Double) {
        timer += delta
        if (timer > 0.05) {
            timer = 0.0
            index = (index+1)

            if (index > letters.length - 1) {
                renderer.text = ""
                index = 0
                iterations++
            }

            if (index %15 == 0) {
                renderer.text += '\n'
            }
            renderer.text += letters[index]
        }


        if (iterations > 100) {
            Game.close()
        }
    }

    override fun render(draw: Drawer) {
        renderer.render()
    }
}
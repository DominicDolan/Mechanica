package com.mechanica.engine.samples.scenes

import com.mechanica.engine.color.Color
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.defaults
import com.mechanica.engine.scenes.scenes.LoadScene
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.logo.MechanicaStartupScreen
import com.mechanica.engine.scenes.setNewMainScene
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.Text

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { LoadSceneDemo() }
        setFullscreen(true)
    }

    Game.loop()
}


class LoadSceneDemo : LoadScene() {

    init {
        addScene(MechanicaStartupScreen(Color.white))
    }

    override fun load() {
        // Assets can be loaded here
        Thread.sleep(6000)
    }

    override fun onFinish() {
        setNewMainScene { ExampleGameScene() }
    }

    override fun render(draw: Drawer) {
        draw.black.background()
    }
}


class ExampleGameScene : Scene() {
    val text = Text("Example Game Scene", Font.defaults.black(true))
    override fun render(draw: Drawer) {
        draw.white.background()
        draw.centered.darkGrey.text(text)
    }
}
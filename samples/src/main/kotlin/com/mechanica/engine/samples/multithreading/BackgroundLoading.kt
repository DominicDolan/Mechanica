package com.mechanica.engine.samples.multithreading

import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.scenes.addAnimation
import com.mechanica.engine.scenes.scenes.LoadScene
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.models.Image

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { BackgroundLoadingDemo() }
    }

    Game.loop()
}

class BackgroundLoadingDemo : Scene() {
    private val imageLoader = addScene(ImageLoader())

    private val dotAnimation = addAnimation(2.0, AnimationFormulas.linear(0.0, 4.0)).also { it.looped = true }
    private val sb = StringBuilder()

    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {
        if (imageLoader.hasFinishedLoading) {
            imageLoader.image?.let {
                draw.centered.image(it)
            }
        } else {
            draw.centered.grey.text("Loading" + getDotString(dotAnimation.value.toInt()), size = 0.5)
        }
    }

    private fun getDotString(count: Int): String {
        sb.clear()
        for (i in 0..count) {
            sb.append('.')
        }
        for (i in count..6) {
            sb.append(' ')
        }
        return sb.toString()
    }
}


class ImageLoader : LoadScene() {
    var image: Image? = null

    var hasFinishedLoading: Boolean = false
        private set

    // load() is called on a different thread
    override fun load() {
        Thread.sleep(4000)
        image = Image.create(Res.image("testImage"))
    }

    // onFinish() is back on the main thread and it is called after load() has finished executing
    override fun onFinish() {
        hasFinishedLoading = true
    }

    override fun render(draw: Drawer) { }
}
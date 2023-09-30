package com.mechanica.engine.samples.image

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.models.Image

fun main() {
    Game.configure {
        setStartingScene { DrawImageScene() }
        setFullscreen(false)
    }

    Game.loop()
}


class DrawImageScene : Scene() {
    private val jump: Image = Image.create(Res.image["testImage"])

    init {

//        val path = "/home/doghouse/IdeaProjects/Mechanica/samples/src/main/resources/res/images/testImage.png"
//        val byteArray = if (SystemFileSystem.exists(Path(path))) {
//            val source = SystemFileSystem.source(Path(path))
//            source.buffered().readByteArray()
//        } else throw FileNotFoundException("File not found at $path")
//
//        val buffer = ByteBuffer.allocateDirect(byteArray.size)
//        buffer.put(byteArray)
//        buffer.position(0)


//        val image = ImageData(buffer)

//        println(image.width)

    }
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        draw.image(jump, 0, 0, 100, 100)
    }
}
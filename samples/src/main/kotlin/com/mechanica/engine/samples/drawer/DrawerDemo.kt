package com.mechanica.engine.samples.drawer

import com.cave.library.angle.degrees
import com.cave.library.color.Color
import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.models.PolygonModel

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        configureDebugMode { screenLog = true }
    }

    val draw = Drawer.create()
    val image = Image.create(Res.image["testImage"])

    val points = arrayOf(
            vec(0, 0),
            vec(0.5, 0.1),
            vec(1.0, 0.0),
            vec(0.5, 0.5),
            vec(1.0, 0.7),
            vec(1.0, 1.0),
            vec(0.7, 1.0),
            vec(0.7, 0.65),
            vec(0.0, 1.0),
            vec(0.2, 0.5),
            vec(0.0, 0.0)
    ).toList()

    val polygonModel = PolygonModel(points)


    Game.loop {
        val rotation = (mouse.x*20.0).degrees
        val radius = ((mouse.y + Game.world.height/2.0)/5.0)
        ScreenLog { "Radius: $radius" }

        draw.red.radius(radius).circle()
        draw.black.alpha(0.5).rotated(rotation).about(1, 2).rectangle(-2, -2, 2 , 4)
        draw.radius(0.2).blue.rectangle(0, -1, 2.0, 1.0)
        draw.depth(radius*2).radius(0.2).image(image, 3, 3)
        draw.radius(0.2).image(image, 3, 3)
        draw.origin.normalized(0.5, 0.5).green.text("Hello,\nworld\nHi", radius, 1, 1)
        draw.transformed.scale(3, 3).translate(4, -2).cyan.polygon(polygonModel)
        draw.color.strokeColor(Color.hex(0xD0F045FF), strokeWidth = 0.05).path(points)
        draw.cyan.ellipse(-3, -3, 2, 1)
    }
}

val mouse: InlineVector
    get() = vec(Mouse.world.x, Mouse.world.y)
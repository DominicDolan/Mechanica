package demo.drawer

import debug.ScreenLog
import drawer.Drawer2
import game.Game
import geometry.lines.LineSegment
import gl.models.PolygonModel
import gl.utils.loadImage
import input.Mouse
import org.lwjgl.opengl.GL11
import resources.Res
import util.colors.hex
import util.extensions.degrees
import util.extensions.radians
import util.extensions.vec
import util.units.LightweightVector

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        configureDebugMode { screenLog = true }
    }

    val draw = Drawer2.create()
    val image = loadImage(Res.image["testImage"])

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


    Game.run {
        val rotation = (mouse.x*20.0).degrees
        val radius = ((mouse.y + Game.view.height/2.0)/5.0)
        ScreenLog { "Radius: $radius" }

        draw.red.radius(radius).circle()
        draw.black.alpha(0.5).rotated(rotation).about(0.5, 0.5).rectangle(-2, -2, 2 , 4)
        draw.radius(0.2).blue.rectangle(0, -1, 2.0, 1.0)
        draw.depth(radius*2).radius(0.2).image(image, 3, 3)
        draw.radius(0.2).image(image, 3, 3)
        draw.layout.origin(0.5, 0.5).green.text("Hello,\nworld\nHi", radius, 1, 1)
        draw.transformed.scale(3, 3).translate(4, -2).cyan.polygon(polygonModel)
        draw.color.strokeColor(hex(0xD0F045FF), strokeWidth = 0.05).path(points)
    }
}

val mouse: LightweightVector
    get() = vec(Mouse.worldX, Mouse.worldY)
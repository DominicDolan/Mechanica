package com.mechanica.engine.samples.temp

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import kotlin.math.cos
import kotlin.math.sin

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)

    }

    val draw = Drawer.create()
    var angle = Math.toRadians(30.0)
    val matrix = Matrix4().identity().customRotate(angle, 0.0, 0.0, 1.0)
    matrix.identity().customRotate(angle*2)
//    val matrix = Matrix4().identity().rotate(degrees30, 0f, 0f, 1f)

    Game.loop {
        draw.red.rectangle(0, 0, 2, 1)
        draw.green.transformed(matrix).rectangle()

    }
}

private fun Matrix4.customRotate(radians: Double, x: Double, y: Double, z: Double): Matrix4 {
    val sin = sin(radians)
    val cos = cos(radians)

    val c = 1.0 - cos

    val xy = x * y * c
    val xz = x * z * c
    val yz = y * z * c

    m00((cos + x * x * c).toFloat()); m10((xy - z * sin).toFloat());    m20((xz + y * sin).toFloat())
    m01((xy + z * sin).toFloat());    m11((cos + y * y * c).toFloat()); m21((yz - x * sin).toFloat())
    m02((xz - y * sin).toFloat());    m12((yz + x * sin).toFloat());    m22((cos + z * z * c).toFloat())

    return this
}

private fun Matrix4.customRotate(radians: Double): Matrix4 {
    val sin = sin(radians)
    val cos = cos(radians)

    val c = 1.0 - cos

    val x = m12() - m21()
    val y = m20() - m02()
    val z = m01() - m10()

    m00((cos + x * x * c).toFloat())
    m11((cos + y * y * c).toFloat())
    m22((cos + z * z * c).toFloat())

    return this
}

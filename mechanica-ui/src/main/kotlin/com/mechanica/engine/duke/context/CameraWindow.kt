package com.mechanica.engine.duke.context

import com.duke.ui.hierarchy.Window
import com.mechanica.engine.game.view.Camera

class CameraWindow(private val camera: Camera) : Window(-camera.width/2.0, -camera.height/2.0, camera.width, camera.height) {
    override val bottom: Double
        get() = camera.bottom
    override val left: Double
        get() = camera.left
    override val right: Double
        get() = camera.right
    override val top: Double
        get() = camera.top
}
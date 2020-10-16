package com.mechanica.engine.game.view

import com.mechanica.engine.display.Window

class ResolutionConverter(var resolutionWidth: Int, var resolutionHeight: Int,
                          var cameraWidth: Double? = null, var cameraHeight: Double? = null) {


    var cameraWidthOut: Double = 1.0
        private set
    var cameraHeightOut: Double = 1.0
        private set

    val ratio: Double
        get() = if (resolutionWidth != 0 && resolutionHeight != 0) {
            resolutionHeight.toDouble()/resolutionWidth.toDouble()
        } else 1.0

    init {
        calculate()
    }

    fun calculate() {
        val viewWidth = this.cameraWidth
        val viewHeight = this.cameraHeight

        if ((viewWidth == null)&&(viewHeight == null)){
            cameraWidthOut = resolutionWidth.toDouble()
            cameraHeightOut = resolutionHeight.toDouble()
        }else if ((viewWidth == null)||(viewHeight == null)) {
            if (viewWidth != null) {
                cameraWidthOut = viewWidth
                cameraHeightOut = viewWidth*ratio
            } else if (viewHeight != null) {
                cameraHeightOut = viewHeight
                cameraWidthOut = viewHeight/ratio
            }
        } else {
            cameraWidthOut = viewWidth
            cameraHeightOut = viewHeight
        }
    }

    fun calculate(camera: Camera, window: Window) {
        if (cameraHeight != null) {
            cameraHeight = camera.height
        }
        if (cameraWidth != null) {
            cameraWidth = camera.width
        }
        resolutionWidth = window.width
        resolutionHeight = window.height

        calculate()
    }

}
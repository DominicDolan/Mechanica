package com.mechanica.engine.game.view

import com.mechanica.engine.display.Window

class ResolutionConverter(var resolutionWidth: Int, var resolutionHeight: Int,
                          var viewWidth: Double? = null, var viewHeight: Double? = null) {


    var viewWidthOut: Double = 1.0
        private set
    var viewHeightOut: Double = 1.0
        private set

    val ratio get() = resolutionHeight.toDouble()/resolutionWidth.toDouble()

    init {
        calculate()
    }

    fun calculate() {
        val viewWidth = this.viewWidth
        val viewHeight = this.viewHeight

        if ((viewWidth == null)&&(viewHeight == null)){
            viewWidthOut = resolutionWidth.toDouble()
            viewHeightOut = resolutionHeight.toDouble()
        }else if ((viewWidth == null)||(viewHeight == null)) {
            if (viewWidth != null) {
                viewWidthOut = viewWidth
                viewHeightOut = viewWidth*ratio
            } else if (viewHeight != null) {
                viewHeightOut = viewHeight
                viewWidthOut = viewHeight/ratio
            }
        } else {
            viewWidthOut = viewWidth
            viewHeightOut = viewHeight
        }
    }

    fun calculate(camera: Camera, window: Window) {
        if (viewHeight != null) {
            viewHeight = camera.height
        }
        if (viewWidth != null) {
            viewWidth = camera.width
        }
        resolutionWidth = window.width
        resolutionHeight = window.height
        calculate()
    }

}
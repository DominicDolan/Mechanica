package com.mechanica.engine.configuration

import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface

interface ContextConfigurationData {
    val display: Display?

    var title: String

    var resolutionWidth: Int?
    var resolutionHeight: Int?

    var viewWidth: Double?
    var viewHeight: Double?

    var viewX: Double
    var viewY: Double

    var fullscreen: Boolean

    var multisamplingSamples: Int

    var surfaceConfiguration: (DrawSurface.() -> Unit)

}
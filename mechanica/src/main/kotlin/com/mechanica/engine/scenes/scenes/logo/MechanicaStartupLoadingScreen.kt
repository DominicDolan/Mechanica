package com.mechanica.engine.scenes.scenes.logo

import com.cave.library.color.Color
import com.mechanica.engine.animation.AnimationSequence
import com.mechanica.engine.scenes.scenes.LoadScene

abstract class MechanicaStartupLoadingScreen(textColor: Color = Color.black, order: Int = 0) : LoadScene(order) {
    private val startupScreen = addScene(MechanicaStartupScreen(textColor))
    val sequence: AnimationSequence
        get() = startupScreen.sequence
}
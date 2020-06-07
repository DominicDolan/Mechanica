package com.mechanica.engine.input.mouse

import com.mechanica.engine.context.callbacks.MouseHandler
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Key
import com.mechanica.engine.input.KeyIDs

internal class MouseImpl : Mouse {
    override val MB1 = Key(KeyIDs.M1)
    override val MB2 = Key(KeyIDs.M2)
    override val MMB = Key(KeyIDs.M3)
    override val M4 = Key(KeyIDs.M4)
    override val M5 = Key(KeyIDs.M5)
    override val M6 = Key(KeyIDs.M6)
    override val M7 = Key(KeyIDs.M7)
    override val M8 = Key(KeyIDs.M8)

    override val scroll = Mouse.ScrollWheel(KeyIDs.SCROLL)
    override val scrollDown = Mouse.ScrollWheel(KeyIDs.SCROLL_DOWN)
    override val scrollUp = Mouse.ScrollWheel(KeyIDs.SCROLL_UP)

    private val worldRatio: Vector = object : Vector {
        override val x: Double
            get() = Game.window.width/ Game.view.width
        override val y: Double
            get() = Game.window.height/ Game.view.height
    }

    private val uiRatio: Vector = object : Vector {
        override val x: Double
            get() = Game.window.width/ Game.ui.width
        override val y: Double
            get() = Game.window.height/ Game.ui.height

    }

    override val pixel: Vector = object : Vector {
        override val x: Double
            get() = MouseHandler.cursorX
        override val y: Double
            get() = MouseHandler.cursorY

    }

    override val world: Vector = object : Vector {
        override val x: Double
            get() = pixel.x/ worldRatio.x - Game.view.width/2.0 + Game.view.x
        override val y: Double
            get() = Game.view.height/2.0 - pixel.y/ worldRatio.y + Game.view.y
    }

    override val ui: Vector = object : Vector {
        override val x: Double
            get() = pixel.x/ uiRatio.x - Game.ui.width/2.0
        override val y: Double
            get() = Game.ui.height/2.0 - pixel.y/ uiRatio.y

    }

}
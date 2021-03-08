package com.mechanica.engine.input.mouse

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.context.callbacks.MouseHandler
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

    private val worldRatio: Vector2 = object : Vector2 {
        override val x: Double
            get() = Game.surface.width/ Game.world.width
        override val y: Double
            get() = Game.surface.height/ Game.world.height
    }

    private val uiRatio: Vector2 = object : Vector2 {
        override val x: Double
            get() = Game.surface.width/ Game.ui.width
        override val y: Double
            get() = Game.surface.height/ Game.ui.height

    }

    override val pixel: Vector2 = object : Vector2 {
        override val x: Double
            get() = MouseHandler.cursorX
        override val y: Double
            get() = MouseHandler.cursorY

    }

    override val world: Vector2 = object : Vector2 {
        override val x: Double
            get() = pixel.x/ worldRatio.x - Game.world.width/2.0 + Game.world.x
        override val y: Double
            get() = Game.world.height/2.0 - pixel.y/ worldRatio.y + Game.world.y
    }

    override val ui: Vector2 = object : Vector2 {
        override val x: Double
            get() = pixel.x/ uiRatio.x - Game.ui.width/2.0
        override val y: Double
            get() = Game.ui.height/2.0 - pixel.y/ uiRatio.y

    }

    override val normalized: Vector2 = object : Vector2 {
        override val x: Double
            get() = pixel.x/ Game.surface.width
        override val y: Double
            get() = 1.0 - pixel.y/ Game.surface.height

    }
}
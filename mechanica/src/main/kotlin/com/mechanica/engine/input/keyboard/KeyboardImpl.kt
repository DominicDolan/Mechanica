package com.mechanica.engine.input.keyboard

import com.mechanica.engine.input.Key
import com.mechanica.engine.input.KeyInput
import com.mechanica.engine.input.Keys

internal class KeyboardImpl : Keyboard {
    override val any: Key by lazy { Key { KeyInput.any } }
    override val A: Key by lazy { Key(Keys.A) }
    override val B: Key by lazy { Key(Keys.B) }
    override val C: Key by lazy { Key(Keys.C) }
    override val D: Key by lazy { Key(Keys.D) }
    override val E: Key by lazy { Key(Keys.E) }
    override val F: Key by lazy { Key(Keys.F) }
    override val G: Key by lazy { Key(Keys.G) }
    override val H: Key by lazy { Key(Keys.H) }
    override val I: Key by lazy { Key(Keys.I) }
    override val J: Key by lazy { Key(Keys.J) }
    override val K: Key by lazy { Key(Keys.K) }
    override val L: Key by lazy { Key(Keys.L) }
    override val M: Key by lazy { Key(Keys.M) }
    override val N: Key by lazy { Key(Keys.N) }
    override val O: Key by lazy { Key(Keys.O) }
    override val P: Key by lazy { Key(Keys.P) }
    override val Q: Key by lazy { Key(Keys.Q) }
    override val R: Key by lazy { Key(Keys.R) }
    override val S: Key by lazy { Key(Keys.S) }
    override val T: Key by lazy { Key(Keys.T) }
    override val U: Key by lazy { Key(Keys.U) }
    override val V: Key by lazy { Key(Keys.V) }
    override val W: Key by lazy { Key(Keys.W) }
    override val X: Key by lazy { Key(Keys.X) }
    override val Y: Key by lazy { Key(Keys.Y) }
    override val Z: Key by lazy { Key(Keys.Z) }

    override val space: Key by lazy { Key(Keys.SPACE) }
    override val shift: Key by lazy { Key(Keys.LSHIFT) }
    override val tab: Key by lazy { Key(Keys.TAB) }
    override val lAlt: Key by lazy { Key(Keys.LALT) }
    override val esc: Key by lazy { Key(Keys.ESC) }
    override val comma: Key by lazy { Key(Keys.COMMA) }
    override val period: Key by lazy { Key(Keys.PERIOD) }
    override val apostrophe: Key by lazy { Key(Keys.APOSTROPHE) }
    override val minus: Key by lazy { Key(Keys.MINUS) }
    override val slash: Key by lazy { Key(Keys.SLASH) }

    override val N0: Key by lazy { Key(Keys.N0) }
    override val N1: Key by lazy { Key(Keys.N1) }
    override val N2: Key by lazy { Key(Keys.N2) }
    override val N3: Key by lazy { Key(Keys.N3) }
    override val N4: Key by lazy { Key(Keys.N4) }
    override val N5: Key by lazy { Key(Keys.N5) }
    override val N6: Key by lazy { Key(Keys.N6) }
    override val N7: Key by lazy { Key(Keys.N7) }
    override val N8: Key by lazy { Key(Keys.N8) }
    override val N9: Key by lazy { Key(Keys.N9) }

    override val semicolon: Key by lazy { Key(Keys.SEMICOLON) }
    override val equal: Key by lazy { Key(Keys.EQUAL) }
    override val leftBracket: Key by lazy { Key(Keys.LEFT_BRACKET) }
    override val backslash: Key by lazy { Key(Keys.BACKSLASH) }
    override val rightBracket: Key by lazy { Key(Keys.RIGHT_BRACKET) }
    override val graveAccent: Key by lazy { Key(Keys.GRAVE_ACCENT) }
    override val world1: Key by lazy { Key(Keys.WORLD_1) }
    override val world2: Key by lazy { Key(Keys.WORLD_2) }
    override val enter: Key by lazy { Key(Keys.ENTER) }
    override val backspace: Key by lazy { Key(Keys.BACKSPACE) }
    override val insert: Key by lazy { Key(Keys.INSERT) }
    override val delete: Key by lazy { Key(Keys.DELETE) }
    override val right: Key by lazy { Key(Keys.RIGHT) }
    override val left: Key by lazy { Key(Keys.LEFT) }
    override val down: Key by lazy { Key(Keys.DOWN) }
    override val up: Key by lazy { Key(Keys.UP) }
    override val pageUp: Key by lazy { Key(Keys.PAGE_UP) }
    override val pageDown: Key by lazy { Key(Keys.PAGE_DOWN) }
    override val home: Key by lazy { Key(Keys.HOME) }
    override val end: Key by lazy { Key(Keys.END) }
    override val capsLock: Key by lazy { Key(Keys.CAPS_LOCK) }
    override val scrollLock: Key by lazy { Key(Keys.SCROLL_LOCK) }
    override val numLock: Key by lazy { Key(Keys.NUM_LOCK) }
    override val printScreen: Key by lazy { Key(Keys.PRINT_SCREEN) }
    override val pause: Key by lazy { Key(Keys.PAUSE) }

    override val F1: Key by lazy { Key(Keys.F1) }
    override val F2: Key by lazy { Key(Keys.F2) }
    override val F3: Key by lazy { Key(Keys.F3) }
    override val F4: Key by lazy { Key(Keys.F4) }
    override val F5: Key by lazy { Key(Keys.F5) }
    override val F6: Key by lazy { Key(Keys.F6) }
    override val F7: Key by lazy { Key(Keys.F7) }
    override val F8: Key by lazy { Key(Keys.F8) }
    override val F9: Key by lazy { Key(Keys.F9) }
    override val F10: Key by lazy { Key(Keys.F10) }
    override val F11: Key by lazy { Key(Keys.F11) }
    override val F12: Key by lazy { Key(Keys.F12) }

    override val lShift: Key by lazy { Key(Keys.LSHIFT) }
    override val lCtrl: Key by lazy { Key(Keys.LCTRL) }
    override val ctrl: Key by lazy { Key(Keys.LCTRL, Keys.RCTRL) }
    override val leftSuper: Key by lazy { Key(Keys.LEFT_SUPER) }
    override val rShift: Key by lazy { Key(Keys.RSHIFT) }
    override val rCtrl: Key by lazy { Key(Keys.RCTRL) }
    override val rAlt: Key by lazy { Key(Keys.RALT) }
    override val rightSuper: Key by lazy { Key(Keys.RIGHT_SUPER) }
    override val menu: Key by lazy { Key(Keys.MENU) }
}
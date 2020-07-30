package com.mechanica.engine.input.keyboard

import com.mechanica.engine.input.Key
import com.mechanica.engine.input.KeyInput
import com.mechanica.engine.input.KeyIDs

internal class KeyboardImpl : Keyboard {
    override val any: Key by lazy { Key("any") { KeyInput.any } }
    override val A: Key by lazy { Key(KeyIDs.A) }
    override val B: Key by lazy { Key(KeyIDs.B) }
    override val C: Key by lazy { Key(KeyIDs.C) }
    override val D: Key by lazy { Key(KeyIDs.D) }
    override val E: Key by lazy { Key(KeyIDs.E) }
    override val F: Key by lazy { Key(KeyIDs.F) }
    override val G: Key by lazy { Key(KeyIDs.G) }
    override val H: Key by lazy { Key(KeyIDs.H) }
    override val I: Key by lazy { Key(KeyIDs.I) }
    override val J: Key by lazy { Key(KeyIDs.J) }
    override val K: Key by lazy { Key(KeyIDs.K) }
    override val L: Key by lazy { Key(KeyIDs.L) }
    override val M: Key by lazy { Key(KeyIDs.M) }
    override val N: Key by lazy { Key(KeyIDs.N) }
    override val O: Key by lazy { Key(KeyIDs.O) }
    override val P: Key by lazy { Key(KeyIDs.P) }
    override val Q: Key by lazy { Key(KeyIDs.Q) }
    override val R: Key by lazy { Key(KeyIDs.R) }
    override val S: Key by lazy { Key(KeyIDs.S) }
    override val T: Key by lazy { Key(KeyIDs.T) }
    override val U: Key by lazy { Key(KeyIDs.U) }
    override val V: Key by lazy { Key(KeyIDs.V) }
    override val W: Key by lazy { Key(KeyIDs.W) }
    override val X: Key by lazy { Key(KeyIDs.X) }
    override val Y: Key by lazy { Key(KeyIDs.Y) }
    override val Z: Key by lazy { Key(KeyIDs.Z) }

    override val space: Key by lazy { Key(KeyIDs.SPACE) }
    override val shift: Key by lazy { Key(KeyIDs.LSHIFT) }
    override val tab: Key by lazy { Key(KeyIDs.TAB) }
    override val lAlt: Key by lazy { Key(KeyIDs.LALT) }
    override val esc: Key by lazy { Key(KeyIDs.ESC) }
    override val comma: Key by lazy { Key(KeyIDs.COMMA) }
    override val period: Key by lazy { Key(KeyIDs.PERIOD) }
    override val apostrophe: Key by lazy { Key(KeyIDs.APOSTROPHE) }
    override val minus: Key by lazy { Key(KeyIDs.MINUS) }
    override val slash: Key by lazy { Key(KeyIDs.SLASH) }

    override val N0: Key by lazy { Key(KeyIDs.N0) }
    override val N1: Key by lazy { Key(KeyIDs.N1) }
    override val N2: Key by lazy { Key(KeyIDs.N2) }
    override val N3: Key by lazy { Key(KeyIDs.N3) }
    override val N4: Key by lazy { Key(KeyIDs.N4) }
    override val N5: Key by lazy { Key(KeyIDs.N5) }
    override val N6: Key by lazy { Key(KeyIDs.N6) }
    override val N7: Key by lazy { Key(KeyIDs.N7) }
    override val N8: Key by lazy { Key(KeyIDs.N8) }
    override val N9: Key by lazy { Key(KeyIDs.N9) }

    override val semicolon: Key by lazy { Key(KeyIDs.SEMICOLON) }
    override val equal: Key by lazy { Key(KeyIDs.EQUAL) }
    override val leftBracket: Key by lazy { Key(KeyIDs.LEFT_BRACKET) }
    override val backslash: Key by lazy { Key(KeyIDs.BACKSLASH) }
    override val rightBracket: Key by lazy { Key(KeyIDs.RIGHT_BRACKET) }
    override val graveAccent: Key by lazy { Key(KeyIDs.GRAVE_ACCENT) }
    override val world1: Key by lazy { Key(KeyIDs.WORLD_1) }
    override val world2: Key by lazy { Key(KeyIDs.WORLD_2) }
    override val enter: Key by lazy { Key(KeyIDs.ENTER) }
    override val backspace: Key by lazy { Key(KeyIDs.BACKSPACE) }
    override val insert: Key by lazy { Key(KeyIDs.INSERT) }
    override val delete: Key by lazy { Key(KeyIDs.DELETE) }
    override val right: Key by lazy { Key(KeyIDs.RIGHT) }
    override val left: Key by lazy { Key(KeyIDs.LEFT) }
    override val down: Key by lazy { Key(KeyIDs.DOWN) }
    override val up: Key by lazy { Key(KeyIDs.UP) }
    override val pageUp: Key by lazy { Key(KeyIDs.PAGE_UP) }
    override val pageDown: Key by lazy { Key(KeyIDs.PAGE_DOWN) }
    override val home: Key by lazy { Key(KeyIDs.HOME) }
    override val end: Key by lazy { Key(KeyIDs.END) }
    override val capsLock: Key by lazy { Key(KeyIDs.CAPS_LOCK) }
    override val scrollLock: Key by lazy { Key(KeyIDs.SCROLL_LOCK) }
    override val numLock: Key by lazy { Key(KeyIDs.NUM_LOCK) }
    override val printScreen: Key by lazy { Key(KeyIDs.PRINT_SCREEN) }
    override val pause: Key by lazy { Key(KeyIDs.PAUSE) }

    override val F1: Key by lazy { Key(KeyIDs.F1) }
    override val F2: Key by lazy { Key(KeyIDs.F2) }
    override val F3: Key by lazy { Key(KeyIDs.F3) }
    override val F4: Key by lazy { Key(KeyIDs.F4) }
    override val F5: Key by lazy { Key(KeyIDs.F5) }
    override val F6: Key by lazy { Key(KeyIDs.F6) }
    override val F7: Key by lazy { Key(KeyIDs.F7) }
    override val F8: Key by lazy { Key(KeyIDs.F8) }
    override val F9: Key by lazy { Key(KeyIDs.F9) }
    override val F10: Key by lazy { Key(KeyIDs.F10) }
    override val F11: Key by lazy { Key(KeyIDs.F11) }
    override val F12: Key by lazy { Key(KeyIDs.F12) }

    override val lShift: Key by lazy { Key(KeyIDs.LSHIFT) }
    override val lCtrl: Key by lazy { Key(KeyIDs.LCTRL) }
    override val ctrl: Key by lazy { Key(KeyIDs.LCTRL, KeyIDs.RCTRL) }
    override val leftSuper: Key by lazy { Key(KeyIDs.LEFT_SUPER) }
    override val rShift: Key by lazy { Key(KeyIDs.RSHIFT) }
    override val rCtrl: Key by lazy { Key(KeyIDs.RCTRL) }
    override val rAlt: Key by lazy { Key(KeyIDs.RALT) }
    override val rightSuper: Key by lazy { Key(KeyIDs.RIGHT_SUPER) }
    override val menu: Key by lazy { Key(KeyIDs.MENU) }
}
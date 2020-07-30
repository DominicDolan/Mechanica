@file:Suppress("unused") // There will be many variables here that go unused most of the time
package com.mechanica.engine.input.keyboard

import com.mechanica.engine.input.Key
import java.util.ArrayList


interface Keyboard {
    val any: Key
    val A: Key
    val B: Key
    val C: Key
    val D: Key
    val E: Key
    val F: Key
    val G: Key
    val H: Key
    val I: Key
    val J: Key
    val K: Key
    val L: Key
    val M: Key
    val N: Key
    val O: Key
    val P: Key
    val Q: Key
    val R: Key
    val S: Key
    val T: Key
    val U: Key
    val V: Key
    val W: Key
    val X: Key
    val Y: Key
    val Z: Key
    val space: Key
    val shift: Key
    val tab: Key
    val lAlt: Key
    val esc: Key
    val comma: Key
    val period: Key
    val apostrophe: Key
    val minus: Key
    val slash: Key
    val N0: Key
    val N1: Key
    val N2: Key
    val N3: Key
    val N4: Key
    val N5: Key
    val N6: Key
    val N7: Key
    val N8: Key
    val N9: Key
    val semicolon: Key
    val equal: Key
    val leftBracket: Key
    val backslash: Key
    val rightBracket: Key
    val graveAccent: Key
    val world1: Key
    val world2: Key
    val enter: Key
    val backspace: Key
    val insert: Key
    val delete: Key
    val right: Key
    val left: Key
    val down: Key
    val up: Key
    val pageUp: Key
    val pageDown: Key
    val home: Key
    val end: Key
    val capsLock: Key
    val scrollLock: Key
    val numLock: Key
    val printScreen: Key
    val pause: Key
    val F1: Key
    val F2: Key
    val F3: Key
    val F4: Key
    val F5: Key
    val F6: Key
    val F7: Key
    val F8: Key
    val F9: Key
    val F10: Key
    val F11: Key
    val F12: Key
    val lShift: Key
    val lCtrl: Key
    val ctrl: Key
    val leftSuper: Key
    val rShift: Key
    val rCtrl: Key
    val rAlt: Key
    val rightSuper: Key
    val menu: Key

    companion object : Keyboard by KeyboardImpl() {
        fun create(): Keyboard = KeyboardImpl()
    }
}
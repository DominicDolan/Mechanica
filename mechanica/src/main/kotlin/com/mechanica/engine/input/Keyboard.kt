@file:Suppress("unused") // There will be many variables here that go unused most of the time
package com.mechanica.engine.input

import com.mechanica.engine.util.codepointToString
import java.util.*
import kotlin.collections.HashMap


object Keyboard {
    private val map = HashMap<Int, ArrayList<Key>>()

    val pressed: Iterable<Key> = PressedKeys(map)

    val textInput = TextInput()

    val any = KeyboardKey()

    val A = KeyboardKey(Keys.A)
    val B = KeyboardKey(Keys.B)
    val C = KeyboardKey(Keys.C)
    val D = KeyboardKey(Keys.D)
    val E = KeyboardKey(Keys.E)
    val F = KeyboardKey(Keys.F)
    val G = KeyboardKey(Keys.G)
    val H = KeyboardKey(Keys.H)
    val I = KeyboardKey(Keys.I)
    val J = KeyboardKey(Keys.J)
    val K = KeyboardKey(Keys.K)
    val L = KeyboardKey(Keys.L)
    val M = KeyboardKey(Keys.M)
    val N = KeyboardKey(Keys.N)
    val O = KeyboardKey(Keys.O)
    val P = KeyboardKey(Keys.P)
    val Q = KeyboardKey(Keys.Q)
    val R = KeyboardKey(Keys.R)
    val S = KeyboardKey(Keys.S)
    val T = KeyboardKey(Keys.T)
    val U = KeyboardKey(Keys.U)
    val V = KeyboardKey(Keys.V)
    val W = KeyboardKey(Keys.W)
    val X = KeyboardKey(Keys.X)
    val Y = KeyboardKey(Keys.Y)
    val Z = KeyboardKey(Keys.Z)

    val space = KeyboardKey(Keys.SPACE)
    val shift = KeyboardKey(Keys.LSHIFT)
    val tab = KeyboardKey(Keys.TAB)
    val lAlt = KeyboardKey(Keys.LALT)
    val esc = KeyboardKey(Keys.ESC)
    val comma = KeyboardKey(Keys.COMMA)
    val period = KeyboardKey(Keys.PERIOD)

    val apostrophe = KeyboardKey(Keys.APOSTROPHE)
    val minus = KeyboardKey(Keys.MINUS)
    val slash = KeyboardKey(Keys.SLASH)
    val N0 = KeyboardKey(Keys.N0)
    val N1 = KeyboardKey(Keys.N1)
    val N2 = KeyboardKey(Keys.N2)
    val N3 = KeyboardKey(Keys.N3)
    val N4 = KeyboardKey(Keys.N4)
    val N5 = KeyboardKey(Keys.N5)
    val N6 = KeyboardKey(Keys.N6)
    val N7 = KeyboardKey(Keys.N7)
    val N8 = KeyboardKey(Keys.N8)
    val N9 = KeyboardKey(Keys.N9)
    val semicolon = KeyboardKey(Keys.SEMICOLON)
    val equal = KeyboardKey(Keys.EQUAL)

    val leftBracket = KeyboardKey(Keys.LEFT_BRACKET)
    val backslash = KeyboardKey(Keys.BACKSLASH)
    val rightBracket = KeyboardKey(Keys.RIGHT_BRACKET)
    val graveAccent = KeyboardKey(Keys.GRAVE_ACCENT)
    val world1 = KeyboardKey(Keys.WORLD_1)
    val world2 = KeyboardKey(Keys.WORLD_2)
    val enter = KeyboardKey(Keys.ENTER)
    val backspace = KeyboardKey(Keys.BACKSPACE)
    val insert = KeyboardKey(Keys.INSERT)
    val delete = KeyboardKey(Keys.DELETE)
    val right = KeyboardKey(Keys.RIGHT)
    val left = KeyboardKey(Keys.LEFT)
    val down = KeyboardKey(Keys.DOWN)
    val up = KeyboardKey(Keys.UP)
    val pageUp = KeyboardKey(Keys.PAGE_UP)
    val pageDown = KeyboardKey(Keys.PAGE_DOWN)
    val home = KeyboardKey(Keys.HOME)
    val end = KeyboardKey(Keys.END)
    val capsLock = KeyboardKey(Keys.CAPS_LOCK)
    val scrollLock = KeyboardKey(Keys.SCROLL_LOCK)
    val numLock = KeyboardKey(Keys.NUM_LOCK)
    val printScreen = KeyboardKey(Keys.PRINT_SCREEN)
    val pause = KeyboardKey(Keys.PAUSE)

    val F1 = KeyboardKey(Keys.F1)
    val F2 = KeyboardKey(Keys.F2)
    val F3 = KeyboardKey(Keys.F3)
    val F4 = KeyboardKey(Keys.F4)
    val F5 = KeyboardKey(Keys.F5)
    val F6 = KeyboardKey(Keys.F6)
    val F7 = KeyboardKey(Keys.F7)
    val F8 = KeyboardKey(Keys.F8)
    val F9 = KeyboardKey(Keys.F9)
    val F10 = KeyboardKey(Keys.F10)
    val F11 = KeyboardKey(Keys.F11)
    val F12 = KeyboardKey(Keys.F12)

    val lShift = KeyboardKey(Keys.LSHIFT)
    val lCtrl = KeyboardKey(Keys.LCTRL)
    val ctrl = KeyboardKey(Keys.LCTRL, Keys.RCTRL)
    val leftSuper = KeyboardKey(Keys.LEFT_SUPER)
    val rShift = KeyboardKey(Keys.RSHIFT)
    val rCtrl = KeyboardKey(Keys.RCTRL)
    val rAlt = KeyboardKey(Keys.RALT)
    val rightSuper = KeyboardKey(Keys.RIGHT_SUPER)
    val menu = KeyboardKey(Keys.MENU)

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    internal fun addPressed(key: Int) {
        val pressed = (pressed as PressedKeys)
        val pressedCount = pressed.size
        get(key).forEach { it.isDown = true }
        pressed.add(key)
        any.isDown = pressed.size > pressedCount
    }

    internal fun removePressed(key: Int) {
        val pressed = (pressed as PressedKeys)
        val pressedCount = pressed.size
        get(key).forEach { it.isDown = false }
        pressed.remove(key)
        any.isDown = pressed.size >= pressedCount
    }

    class KeyboardKey(vararg keys: Keys): Key(map, *keys)

    class TextInput {

        private val emptyString = ""

        var hasBeenInput: String = emptyString
            get() {
                val value = field
                field = emptyString
                return value
            }
            private set


        private val inputCharacterHistory = Array(1024) { emptyString }

        var inputText = emptyString
            private set(value) {
                field = value
                hasBeenInput = value
            }

        internal fun setInputTextChar(codepoint: Int) {
            var characters = inputCharacterHistory[codepoint]
            if (characters == emptyString) {
                characters = codepointToString(codepoint)
                inputCharacterHistory[codepoint] = characters
            }
            inputText = characters
        }
    }
}
@file:Suppress("unused") // There will be many variables here that go unused most of the time
package com.mechanica.engine.input

import com.mechanica.engine.util.codepointToString
import java.util.*
import kotlin.collections.HashMap


object Keyboard {
    private val map = HashMap<Int, ArrayList<Key>>()

    val pressed: Iterable<Key> = PressedKeys(map)

    val textInput = TextInput()

    val ANY = KeyboardKey()

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

    val MB1 = KeyboardKey(Keys.M1)
    val MB2 = KeyboardKey(Keys.M2)
    val MMB = KeyboardKey(Keys.M3)

    val SPACE = KeyboardKey(Keys.SPACE)
    val SHIFT = KeyboardKey(Keys.LSHIFT)
    val TAB = KeyboardKey(Keys.TAB)
    val LALT = KeyboardKey(Keys.LALT)
    val ESC = KeyboardKey(Keys.ESC)
    val COMMA = KeyboardKey(Keys.COMMA)
    val PERIOD = KeyboardKey(Keys.PERIOD)

    val APOSTROPHE = KeyboardKey(Keys.APOSTROPHE)
    val MINUS = KeyboardKey(Keys.MINUS)
    val SLASH = KeyboardKey(Keys.SLASH)
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
    val SEMICOLON = KeyboardKey(Keys.SEMICOLON)
    val EQUAL = KeyboardKey(Keys.EQUAL)

    val LEFT_BRACKET = KeyboardKey(Keys.LEFT_BRACKET)
    val BACKSLASH = KeyboardKey(Keys.BACKSLASH)
    val RIGHT_BRACKET = KeyboardKey(Keys.RIGHT_BRACKET)
    val GRAVE_ACCENT = KeyboardKey(Keys.GRAVE_ACCENT)
    val WORLD_1 = KeyboardKey(Keys.WORLD_1)
    val WORLD_2 = KeyboardKey(Keys.WORLD_2)
    val ENTER = KeyboardKey(Keys.ENTER)
    val BACKSPACE = KeyboardKey(Keys.BACKSPACE)
    val INSERT = KeyboardKey(Keys.INSERT)
    val DELETE = KeyboardKey(Keys.DELETE)
    val RIGHT = KeyboardKey(Keys.RIGHT)
    val LEFT = KeyboardKey(Keys.LEFT)
    val DOWN = KeyboardKey(Keys.DOWN)
    val UP = KeyboardKey(Keys.UP)
    val PAGE_UP = KeyboardKey(Keys.PAGE_UP)
    val PAGE_DOWN = KeyboardKey(Keys.PAGE_DOWN)
    val HOME = KeyboardKey(Keys.HOME)
    val END = KeyboardKey(Keys.END)
    val CAPS_LOCK = KeyboardKey(Keys.CAPS_LOCK)
    val SCROLL_LOCK = KeyboardKey(Keys.SCROLL_LOCK)
    val NUM_LOCK = KeyboardKey(Keys.NUM_LOCK)
    val PRINT_SCREEN = KeyboardKey(Keys.PRINT_SCREEN)
    val PAUSE = KeyboardKey(Keys.PAUSE)

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

    val LSHIFT = KeyboardKey(Keys.LSHIFT)
    val LCTRL = KeyboardKey(Keys.LCTRL)
    val CTRL = KeyboardKey(Keys.LCTRL, Keys.RCTRL)
    val LEFT_SUPER = KeyboardKey(Keys.LEFT_SUPER)
    val RSHIFT = KeyboardKey(Keys.RSHIFT)
    val RCTRL = KeyboardKey(Keys.RCTRL)
    val RALT = KeyboardKey(Keys.RALT)
    val RIGHT_SUPER = KeyboardKey(Keys.RIGHT_SUPER)
    val MENU = KeyboardKey(Keys.MENU)

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
        ANY.isDown = pressed.size > pressedCount
    }

    internal fun removePressed(key: Int) {
        val pressed = (pressed as PressedKeys)
        val pressedCount = pressed.size
        get(key).forEach { it.isDown = false }
        pressed.remove(key)
        ANY.isDown = pressed.size >= pressedCount
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
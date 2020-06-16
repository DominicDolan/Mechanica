package com.mechanica.engine.input

import com.mechanica.engine.context.loader.GLLoader

interface KeyIDs {
    val UNKNOWN: KeyID
    val M1: KeyID
    val M2: KeyID
    val M3: KeyID
    val M4: KeyID
    val M5: KeyID
    val M6: KeyID
    val M7: KeyID
    val M8: KeyID
    val SPACE: KeyID
    val APOSTROPHE: KeyID
    val COMMA: KeyID
    val MINUS: KeyID
    val PERIOD: KeyID
    val SLASH: KeyID
    val N0: KeyID
    val N1: KeyID
    val N2: KeyID
    val N3: KeyID
    val N4: KeyID
    val N5: KeyID
    val N6: KeyID
    val N7: KeyID
    val N8: KeyID
    val N9: KeyID
    val SEMICOLON: KeyID
    val EQUAL: KeyID
    val A: KeyID
    val B: KeyID
    val C: KeyID
    val D: KeyID
    val E: KeyID
    val F: KeyID
    val G: KeyID
    val H: KeyID
    val I: KeyID
    val J: KeyID
    val K: KeyID
    val L: KeyID
    val M: KeyID
    val N: KeyID
    val O: KeyID
    val P: KeyID
    val Q: KeyID
    val R: KeyID
    val S: KeyID
    val T: KeyID
    val U: KeyID
    val V: KeyID
    val W: KeyID
    val X: KeyID
    val Y: KeyID
    val Z: KeyID
    val LEFT_BRACKET: KeyID
    val BACKSLASH: KeyID
    val RIGHT_BRACKET: KeyID
    val GRAVE_ACCENT: KeyID
    val WORLD_1: KeyID
    val WORLD_2: KeyID
    val ESC: KeyID
    val ENTER: KeyID
    val TAB: KeyID
    val BACKSPACE: KeyID
    val INSERT: KeyID
    val DELETE: KeyID
    val RIGHT: KeyID
    val LEFT: KeyID
    val DOWN: KeyID
    val UP: KeyID
    val PAGE_UP: KeyID
    val PAGE_DOWN: KeyID
    val HOME: KeyID
    val END: KeyID
    val CAPS_LOCK: KeyID
    val SCROLL_LOCK: KeyID
    val NUM_LOCK: KeyID
    val PRINT_SCREEN: KeyID
    val PAUSE: KeyID
    val F1: KeyID
    val F2: KeyID
    val F3: KeyID
    val F4: KeyID
    val F5: KeyID
    val F6: KeyID
    val F7: KeyID
    val F8: KeyID
    val F9: KeyID
    val F10: KeyID
    val F11: KeyID
    val F12: KeyID
    val F13: KeyID
    val F14: KeyID
    val F15: KeyID
    val F16: KeyID
    val F17: KeyID
    val F18: KeyID
    val F19: KeyID
    val F20: KeyID
    val F21: KeyID
    val F22: KeyID
    val F23: KeyID
    val F24: KeyID
    val F25: KeyID
    val KP_0: KeyID
    val KP_1: KeyID
    val KP_2: KeyID
    val KP_3: KeyID
    val KP_4: KeyID
    val KP_5: KeyID
    val KP_6: KeyID
    val KP_7: KeyID
    val KP_8: KeyID
    val KP_9: KeyID
    val KP_DECIMAL: KeyID
    val KP_DIVIDE: KeyID
    val KP_MULTIPLY: KeyID
    val KP_SUBTRACT: KeyID
    val KP_ADD: KeyID
    val KP_ENTER: KeyID
    val KP_EQUAL: KeyID
    val LSHIFT: KeyID
    val LCTRL: KeyID
    val LALT: KeyID
    val LEFT_SUPER: KeyID
    val RSHIFT: KeyID
    val RCTRL: KeyID
    val RALT: KeyID
    val RIGHT_SUPER: KeyID
    val MENU: KeyID

    val SCROLL_UP: KeyID
    val SCROLL_DOWN: KeyID
    val SCROLL: KeyID

    companion object : KeyIDs by GLLoader.inputLoader.keyIds()
}
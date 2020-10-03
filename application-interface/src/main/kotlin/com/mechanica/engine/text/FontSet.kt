package com.mechanica.engine.text

import com.mechanica.engine.resources.Resource

open class FontSet(protected val name: String, protected val configureAtlas: FontAtlasConfiguration.() -> Unit = { }) {
    protected var modifier = Modifiers.REGULAR
    protected var italic = ""

    protected val fileName: String
        get() = "res/fonts/${toString()}.ttf"

    fun regular(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.REGULAR
        return create()
    }

    fun bold(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.BOLD
        return create()
    }

    fun medium(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.MEDIUM
        return create()
    }

    fun black(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.BLACK
        return create()
    }

    fun light(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.LIGHT
        return create()
    }

    fun thin(italic: Boolean): Font {
        if (italic) this.italic = italicString
        modifier = Modifiers.THIN
        return create()
    }

    open fun create() = Font.create(Resource(fileName), configureAtlas)

    override fun toString(): String {
        return "$name-$modifier$italic"
    }

    enum class Modifiers(val string: String) {
        REGULAR("Regular"),
        BOLD("Bold"),
        BLACK("Black"),
        LIGHT("Light"),
        THIN("Thin"),
        MEDIUM("Medium");

        override fun toString(): String {
            return string
        }
    }

    companion object {
        const val italicString = "Italic"
    }
}

class CachedFontSet(name: String, configureAtlas: FontAtlasConfiguration.() -> Unit = { }) : FontSet(name, configureAtlas) {
    override fun create(): Font {
        val fileName = this.fileName
        return fontMap[fileName] ?: super.create().also { fontMap[fileName] = it }
    }

    companion object {
        private val fontMap = HashMap<String, Font>()
    }
}
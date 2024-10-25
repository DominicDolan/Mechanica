package com.mechanica.engine.samples.ui.duke.component

import com.mechanica.engine.samples.ui.duke.DukeUI
import com.mechanica.engine.samples.ui.duke.layout.Layout
import com.mechanica.engine.samples.ui.duke.layout.MutableLayout
import com.mechanica.engine.samples.ui.duke.layout.NodeLayouts
import com.mechanica.engine.samples.ui.duke.theme.Style

sealed class LayoutBuilder() {
    val style: StyleBuilder
        get() = (this as LayoutBuilderImpl).dukeBuilder.style

    inline fun edit(editor: MutableLayout.(p: Layout, s: Layout) -> Unit): LayoutBuilder {
        val layouts = (this as LayoutBuilderImpl).layouts
        require(layouts != null) { "Cannot compute layouts when the relevant layouts are null" }
        editor(layouts.layout, layouts.parent, layouts.sibling)
        return this
    }

    inline fun content(build: ContentBuilder.() -> Unit) {
        (this as LayoutBuilderImpl).dukeBuilder.content(build)
    }
}

class LayoutBuilderImpl(val dukeBuilder: DukeUI.DukeBuilder) : LayoutBuilder() {
    var layouts: NodeLayouts? = null
}

sealed class StyleBuilder() {
    val layout: LayoutBuilder
        get() = (this as StyleBuilderImpl).dukeBuilder.layout

    inline fun edit(editor: Style.() -> Unit): StyleBuilder {
        val style = (this as StyleBuilderImpl).styles
        require(style != null) { "Tried to get style from ComponentContext but it was null, prepare should be called first" }
        editor(style)
        return this
    }

    inline fun content(build: ContentBuilder.() -> Unit) {
        (this as StyleBuilderImpl).dukeBuilder.content(build)
    }
}

class StyleBuilderImpl(val dukeBuilder: DukeUI.DukeBuilder) : StyleBuilder() {
    var styles: Style? = null
}

abstract class ComponentBuilder() {
    abstract val layout: LayoutBuilder
    abstract val style: StyleBuilder

    inline fun content(build: ContentBuilder.() -> Unit) {
        (this as ComponentBuilderImpl).dukeBuilder.appendContent(build)
    }
}


class ComponentBuilderImpl(val dukeBuilder: DukeUI.DukeBuilder) : ComponentBuilder() {
    override val layout
        get() = dukeBuilder.layout
    override val style
        get() = dukeBuilder.style

    fun prepare(layouts: NodeLayouts, style: Style) {
        this.layout.layouts = layouts
        this.style.styles = style
    }
}

class ContentBuilder(private val builder: DukeUI.DukeBuilder) {
    fun appendComponent(): ComponentBuilder {
        return builder.appendComponent()
    }
}


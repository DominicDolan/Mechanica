package com.mechanica.engine.samples.ui.duke.builder

import com.mechanica.engine.samples.ui.duke.DukeUI.DukeBuilder
import com.mechanica.engine.samples.ui.duke.layout.Layout
import com.mechanica.engine.samples.ui.duke.layout.MutableLayout
import com.mechanica.engine.samples.ui.duke.theme.Style

sealed class LayoutBuilder<C>() {
    inline fun edit(editor: MutableLayout.(p: Layout, s: Layout) -> Unit): LayoutBuilder<C> {
        val layouts = (this as LayoutBuilderImpl).dukeBuilder.currentLayouts
        editor(layouts.layout, layouts.parent, layouts.sibling)
        return this
    }

    inline fun content(build: C.() -> Unit) {
        (this as LayoutBuilderImpl).dukeBuilder.buildContent(build)
    }

    fun style(): StyleBuilder<C> {
        return (this as LayoutBuilderImpl).dukeBuilder.style as StyleBuilder<C>
    }
}

class LayoutBuilderImpl(val dukeBuilder: DukeBuilder) : LayoutBuilder<Any>()

sealed class StyleBuilder<C>() {
    inline fun edit(editor: Style.() -> Unit): StyleBuilder<C> {
        val style = (this as StyleBuilderImpl).dukeBuilder.currentStyle
        editor(style)
        return this
    }

    fun theme(hook: String): StyleBuilder<C> {
        (this as StyleBuilderImpl).dukeBuilder.matchThemeHook(hook)
        return this
    }

    inline fun content(build: C.() -> Unit) {
        (this as StyleBuilderImpl).dukeBuilder.buildContent(build)
    }

    fun <T> layout(): LayoutBuilder<C> {
        return (this as StyleBuilderImpl).dukeBuilder.layout as LayoutBuilder<C>
    }
}

class StyleBuilderImpl(val dukeBuilder: DukeBuilder) : StyleBuilder<Any>()

abstract class NodeBuilder<C : ContentBuilder>() {
    inline fun content(build: C.() -> Unit) {
        (this as NodeBuilderImpl).dukeBuilder.buildContent(build)
    }

    fun layout(): LayoutBuilder<C> {
        return (this as NodeBuilderImpl).dukeBuilder.layout as LayoutBuilder<C>
    }

    fun style(): StyleBuilder<C> {
        return (this as NodeBuilderImpl).dukeBuilder.style as StyleBuilder<C>
    }
}
class NodeBuilderImpl(val dukeBuilder: DukeBuilder) : NodeBuilder<ContentBuilder>()

open class ContentBuilder(val builder: DukeBuilder) {
    open fun createNode(): NodeBuilder<ContentBuilder> {
        return builder.createNode()
    }

    inline fun <reified C : ContentBuilder> createNodeWithBuilder(
            contentBuilderConstructor: (DukeBuilder) -> C,
            prepareBuilder: ((C) -> Unit) = {}): NodeBuilder<C> {
        return builder.createNodeWithBuilder(contentBuilderConstructor, prepareBuilder)
    }

    fun triggerOnPostAppend(index: Int) {
        onPostAppend(index)
    }

    protected open fun onPostAppend(index: Int) {

    }
}




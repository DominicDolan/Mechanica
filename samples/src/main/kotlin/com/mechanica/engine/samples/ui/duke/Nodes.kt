package com.mechanica.engine.samples.ui.duke

import com.mechanica.engine.samples.ui.duke.DukeUI.DukeBuilder
import com.mechanica.engine.samples.ui.duke.builder.NodeBuilder
import com.mechanica.engine.samples.ui.duke.builder.ContentBuilder


fun ContentBuilder.h(): NodeBuilder<ContentBuilder> {
    return createNode()
}


class ColumnContentBuilder(dukeBuilder: DukeBuilder) : ContentBuilder(dukeBuilder) {
    var gap: Double = 0.0
    override fun createNode(): NodeBuilder<ContentBuilder> {
        val el = builder.createNode()
        el.layout().edit { p, s ->
            height = height
            width = p.width
            left = p.left
            top = s.bottom + gap
        }

        return el
    }
}

fun ContentBuilder.column(gap: Double): NodeBuilder<ColumnContentBuilder> {
    return createNodeWithBuilder({ ColumnContentBuilder(it) }, { it.gap = gap } )
}

class RowContentBuilder(dukeBuilder: DukeBuilder) : ContentBuilder(dukeBuilder) {
    var gap: Double = 0.0
    override fun createNode(): NodeBuilder<ContentBuilder> {
        val el = builder.createNode()
        el.layout().edit { p, s ->
            width = width
            height = p.height
            top = p.top
            left = s.right + gap
        }

        return el
    }
}

fun ContentBuilder.row(gap: Double): NodeBuilder<RowContentBuilder> {
    return createNodeWithBuilder({ RowContentBuilder(it) }, { it.gap = gap } )
}
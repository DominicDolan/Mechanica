package com.mechanica.engine.ui.elements

import com.dubulduke.ui.layout.EditLayout
import com.dubulduke.ui.layout.Layout
import com.mechanica.engine.text.Font
import com.mechanica.engine.text.Text


inline fun DrawerElement.box(block: DrawerElement.() -> Unit) {
    val boxElement = addChildElement { BoxElement(context) }
    block(boxElement)
}

fun DrawerElement.text(string: String, layout: EditLayout.(p: Layout, s: Layout) -> Unit = {_, _ -> }) {
    val text = addChildElement { TextElement(Text(string, this.style.font ?: Font.defaultFont), context) }
    text.layout.edit(layout)
    text.copyStyle(this.style)
}

inline fun DrawerElement.listItem(height: Double = 1.0, block: DrawerElement.() -> Unit) {
    val listItem = addChildElement { BoxElement(context) }
    listItem.layout.add(listLayout)
    listItem.layout.edit { _, _ -> this.height = height }
    block(listItem)
}

inline fun DrawerElement.tileItem(block: DrawerElement.() -> Unit) {
    val tileItem = addChildElement { BoxElement(context) }
    tileItem.layout.add(tileLayout)
    block(tileItem)
}
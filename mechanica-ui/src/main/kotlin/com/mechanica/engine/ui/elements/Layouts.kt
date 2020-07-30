package com.mechanica.engine.ui.elements

import com.dubulduke.ui.layout.EditLayout
import com.dubulduke.ui.layout.Layout
import com.dubulduke.ui.layout.LayoutSetter

val listLayout = Layout.invoke { _, sibling ->
    this.top = sibling.bottom
}

val tileLayout = Layout.invoke { parent, sibling ->
    if (sibling.right + sibling.width <= parent.right) {
        this.left = sibling.right
        this.top = sibling.top
    } else {
        this.left = parent.left
        this.top = sibling.bottom
    }
}

private val _paddingLayout = PaddingLayout()
val DrawerElement.paddingLayout: PaddingLayout
    get() = _paddingLayout
class PaddingLayout : LayoutSetter {

    private var left: Double = 0.0
    private var top: Double = 0.0
    private var right: Double = 0.0
    private var bottom: Double = 0.0

    override fun EditLayout.perform(parent: Layout, sibling: Layout) {
        val left = this@PaddingLayout.left
        val right = this@PaddingLayout.right
        val top = this@PaddingLayout.top
        val bottom = this@PaddingLayout.bottom

        padding(left, top, right, bottom)

        reset()
    }

    private fun reset() {
        left = 0.0
        top = 0.0
        right = 0.0
        bottom = 0.0
    }

    operator fun invoke(size: Double = 0.0): LayoutSetter {
        left = size
        top = size
        right = size
        bottom = size
        return this
    }

    operator fun invoke(horizontal: Double = 0.0, vertical: Double = 0.0): LayoutSetter {
        left = horizontal
        right = horizontal
        top = vertical
        bottom = vertical
        return this
    }

    operator fun invoke(left: Double = 0.0, top: Double = 0.0, right: Double = 0.0, bottom: Double = 0.0): LayoutSetter {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
        return this
    }
}

private val _scaleToParent = ScaleToParentLayout()
val DrawerElement.scaleToParent: ScaleToParentLayout
    get() = _scaleToParent

class ScaleToParentLayout : LayoutSetter {
    private var widthScale = 1.0
    private var heightScale = 1.0

    override fun EditLayout.perform(parent: Layout, sibling: Layout) {
        this.width = parent.width*widthScale
        this.height = parent.height*heightScale
        reset()
    }

    operator fun invoke(widthScale: Double = 1.0, heightScale: Double = 1.0): LayoutSetter {
        this.widthScale = widthScale
        this.heightScale = heightScale
        return this
    }

    private fun reset() {
        this.widthScale = 1.0
        this.heightScale = 1.0
    }
}

val alignRight = Layout.invoke { p, _ ->
    this.right = p.right
}

val alignLeft = Layout.invoke { p, _ ->
    this.left = p.left
}

val centerHorizontal = Layout.invoke { p, _ ->
    this.center.x = p.center.x
}

fun EditLayout.padding(horizontal: Double = 0.0, vertical: Double = 0.0) {
    left += horizontal
    width -= 2*horizontal
    top += vertical
    height -= 2*vertical
}

fun EditLayout.padding(left: Double = 0.0, top: Double = 0.0, right: Double = 0.0, bottom: Double = 0.0) {
    this.left += left
    this.width -= (right + left)
    this.top += top
    this.height -= (bottom + top)
}

fun EditLayout.padding(size: Double = 0.0) {
    this.left += size
    this.width -= 2*size
    this.top += size
    this.height -= 2*size
}

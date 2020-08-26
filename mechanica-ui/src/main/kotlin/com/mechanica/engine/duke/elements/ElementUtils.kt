package com.mechanica.engine.duke.elements

import com.duke.ui.layout.DynamicLayout
import com.duke.ui.layout.Layout
import com.duke.ui.output.RenderDescription
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.duke.style.Style


fun interface ElementDrawer {
    fun RenderDescription.draw(draw: Drawer, style: Style)
}

inline fun Element.ifHovering(action: () -> Unit) {
    if (isHovering) {
        action()
    }
}

inline fun Element.ifNotHovering(action: () -> Unit) {
    if (!isHovering) {
        action()
    }
}

inline fun Element.animateLayoutUsing(
    animationFormula: AnimationFormula, 
    block: DynamicLayout.(parent: Layout, sibling: Layout) -> Unit) {
    
    val x = layout.x
    val y = layout.y
    
    val width = layout.width
    val height = layout.height
    
    block(layout, layout.parent, layout.sibling)
    
    val value = animationFormula.value
    
    layout.x = x + (layout.x - x)*value
    layout.y = y + (layout.y - y)*value
    layout.width = width + (layout.width - width)*value
    layout.height = height + (layout.height - height)*value
}


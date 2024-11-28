package com.mechanica.engine.samples.ui.duke.layout

import com.mechanica.engine.samples.ui.duke.builder.LayoutBuilder


fun <C> LayoutBuilder<C>.fill() = this.edit { p, s ->
    left = p.left
    right = p.right

    top = p.top
    bottom = p.bottom
}

fun <C> LayoutBuilder<C>.fillWidth() = this.edit { p, s ->
    left = p.left
    right = p.right
}

fun <C> LayoutBuilder<C>.fillHeight() = this.edit { p, s ->
    top = p.top
    bottom = p.bottom
}

fun <C> LayoutBuilder<C>.width(width: Double) = this.edit { p, s ->
    this.width = width
}

fun <C> LayoutBuilder<C>.height(height: Double) = this.edit { p, s ->
    this.height = height
}

fun <C> LayoutBuilder<C>.block(gap: Double = 0.0) = this.edit { p, s ->
    left = s.left
    top = s.bottom + gap
}

fun <C> LayoutBuilder<C>.inline(gap: Double = 0.0) = this.edit { p, s ->
    left = s.right + gap
    top = s.top
}

fun <C> LayoutBuilder<C>.inset(inset: Double) = this.edit { p, s ->
    top -= inset
    bottom -= inset
    left -= inset
    right -= inset
}

fun <C> LayoutBuilder<C>.inset(x: Double = 0.0, y: Double = 0.0) = this.edit { p, s ->
    top -= y
    bottom -= y
    left -= x
    right -= x
}

fun <C> LayoutBuilder<C>.inset(
    top: Double = 0.0,
    right: Double = 0.0,
    bottom: Double = 0.0,
    left: Double = 0.0) = this.edit { p, s ->
    this.top -= top
    this.bottom -= bottom
    this.left -= left
    this.right -= right
}

fun <C> LayoutBuilder<C>.alignTop(offset: Double = 0.0) = this.edit { p, s ->
    top = p.top + offset
}

fun <C> LayoutBuilder<C>.alignBottom(offset: Double = 0.0) = this.edit { p, s ->
    top = p.bottom + offset
}

fun <C> LayoutBuilder<C>.alignLeft(offset: Double = 0.0) = this.edit { p, s ->
    left = p.left + offset
}

fun <C> LayoutBuilder<C>.alignRight(offset: Double = 0.0) = this.edit { p, s ->
    right = p.right + offset
}

fun <C> LayoutBuilder<C>.center() = this.edit { p, s ->
    center.set(p.center)
}

fun <C> LayoutBuilder<C>.centerX() = this.edit { p, s ->
    center.x = p.center.x
}

fun <C> LayoutBuilder<C>.centerY() = this.edit { p, s ->
    center.y = p.center.y
}
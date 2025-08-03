package com.mechanica.engine.drawer.subclasses.stencil

import com.mechanica.engine.context.loader.StencilType
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

open class StencilDrawer(drawer: Drawer, private val state: DrawState) : Drawer by drawer {

    val union: StencilDrawer get() {
        state.stencil.type.variable = StencilType.Union
        return this
    }

    val intersection: StencilDrawer get() {
        state.stencil.type.variable = StencilType.Intersection
        return this
    }

    val difference: StencilDrawer get() {
        state.stencil.type.variable = StencilType.Difference
        return this
    }

    val withColor: StencilDrawer get() {
        state.stencil.isWithColor.value = true
        return this
    }

    val withoutColor: StencilDrawer get() {
        state.stencil.isWithColor.value = false
        return this
    }

    val write: StencilDrawer get() {
        state.stencil.writeMode.value = true
        if (!state.stencil.isWithColor.wasChanged) {
            state.stencil.isWithColor.value = false
        }
        return this
    }

    val read: StencilDrawer get() {
        state.stencil.writeMode.value = false
        if (!state.stencil.isWithColor.wasChanged) {
            state.stencil.isWithColor.value = true
        }
        return this
    }

    fun mask(mask: Int): StencilDrawer {
        state.stencil.mask.value = mask
        return this
    }

    fun reference(ref: Int): StencilDrawer {
        state.stencil.reference.value = ref
        return this
    }

    inline fun useStencil(operation: () -> Unit) {
        val state = (this as? StencilDrawerImpl)?.state
        if (state != null) {
            state.stencil.suppressReset = true
        }
        operation()
        if (state != null) {
            state.stencil.suppressReset = false
            state.stencil.reset()
        }
    }
}
package com.mechanica.engine.context.loader

/**
 * Rectangular clipping.
 *
 * Separate from [StencilFactory] because the two answer different questions. A stencil clips to an
 * arbitrary drawn shape and costs a buffer, a mask and a reference value per nesting level; a
 * scissor clips to one axis-aligned rectangle and costs four integers. UI clipping — panels,
 * scrolling lists, anything with `overflow: hidden` — is always the rectangle case, and nesting is
 * plain intersection, so the caller can flatten any depth into a single rectangle before it gets
 * here.
 */
interface ScissorFactory {
    /**
     * Restrict drawing to a rectangle, in **pixels from the bottom-left of the framebuffer**.
     *
     * Pixels rather than world or UI units, because that is the only space the graphics API has an
     * opinion about; converting is the caller's job and depends on which camera it is drawing
     * through.
     */
    fun enableScissor(x: Int, y: Int, width: Int, height: Int)

    /** Draw to the whole framebuffer again. */
    fun disableScissor()
}

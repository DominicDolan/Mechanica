package com.mechanica.engine.scenes.activation

import com.mechanica.engine.drawer.Drawer

interface Activatable {
    /**
     * Gates this node and everything below it. An inactive node's children are not traversed
     * at all, and the node itself gets [updateWhileInactive] and [renderWhileInactive] in
     * place of its normal update and render.
     */
    val active: Boolean get() = true

    /**
     * Gates this node's own update only. A hub that is not playing still updates its children.
     * To stop a whole subtree, use [active].
     */
    val playing: Boolean get() = true

    /**
     * Gates this node's own render only. A hub that is not visible still renders its children.
     * To hide a whole subtree, use [active].
     */
    val visible: Boolean get() = true

    /**
     * Runs in place of this node's update while it is inactive or not playing, for work that
     * has to keep ticking after the node stops running: an animation playing itself out, a
     * timer winding down.
     */
    fun updateWhileInactive(delta: Double) {}

    /**
     * Runs in place of this node's render while it is inactive or not visible, for effects
     * that outlive the node that spawned them.
     */
    fun renderWhileInactive(draw: Drawer) {}
}

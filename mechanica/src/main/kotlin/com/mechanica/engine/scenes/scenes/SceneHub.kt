package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer

abstract class SceneHub : SceneNode {

    private val childHolders = ArrayList<SceneNodeHolder>()

    private var listHasChanged = true
    private var _children = emptyList<SceneNode>()
    val children: List<SceneNode>
        get() {
            if (listHasChanged) {
                _children = childHolders.map { it.scene }
                listHasChanged = false
            }
            return _children
        }

    private var snapshotHasChanged = true
    private var snapshot: Array<SceneNodeHolder> = emptyArray()

    /**
     * A stable view of the children for the duration of one traversal.
     *
     * A child is free to add or remove scenes from inside its own update, so the list backing
     * this hub can change while it is being walked. Traversing this array instead means an
     * add or a remove cannot shift the list out from under the walk and make it skip a
     * sibling; the change is picked up on the next traversal. Removals take effect
     * immediately even so, because detaching marks the holder and the walk skips it.
     */
    private val traversalSnapshot: Array<SceneNodeHolder>
        get() {
            if (snapshotHasChanged) {
                snapshot = childHolders.toTypedArray()
                snapshotHasChanged = false
            }
            return snapshot
        }

    val hasChildren: Boolean
        get() = childHolders.isNotEmpty()

    /**
     * Adds [scene] as a child of this hub.
     *
     * [order] governs both the traversal position and the render depth: children with a
     * negative order are updated and rendered before this hub's own update and render, and
     * the rest afterwards. So a child this hub reads during its own update belongs at a
     * negative order, and a child that must draw on top of it belongs at a non negative one.
     */
    fun <S:SceneNode> addScene(scene: S, order: Int = 0): S {
        if (!hasScene(scene)) {
            childHolders.add(SceneNodeHolder(scene, order))
            childHolders.sortBy { it.order }
            invalidateChildren()
        } else {
            throw IllegalArgumentException("Cannot add scene because it has already been added")
        }
        return scene
    }

    private fun invalidateChildren() {
        listHasChanged = true
        snapshotHasChanged = true
    }

    /**
     * Detaches [scene] from this hub and notifies its entire subtree, deepest first.
     * Every detached node receives [SceneNode.onRemove] exactly once.
     */
    fun removeScene(scene: SceneNode): Boolean {
        val index = childHolders.indexOfFirst { it.scene === scene }
        if (index == -1) return false

        childHolders.removeAt(index).detached = true
        invalidateChildren()
        scene.detach()
        return true
    }

    fun <S : SceneNode> replaceScene(old: S, new: S): S {
        val index = childHolders.indexOfFirst { it.scene === old }
        if (index != -1) {
            val order = childHolders[index].order
            childHolders.removeAt(index).detached = true

            childHolders.add(index, SceneNodeHolder(new, order))
            childHolders.sortBy { it.order }
            invalidateChildren()

            old.detach()
            return new
        }
        return old
    }

    fun hasScene(scene: SceneNode): Boolean {
        return childHolders.find { it.scene === scene } != null
    }

    /**
     * Detaches every child of this hub, notifying each subtree exactly once.
     * The children are removed from the hub before they are notified, so calling
     * this a second time is a no-op rather than a second round of [SceneNode.onRemove].
     */
    internal fun removeChildren() {
        if (childHolders.isEmpty()) return

        val detaching = childHolders.toList()
        childHolders.clear()
        invalidateChildren()

        for (i in detaching.indices) {
            detaching[i].detached = true
            detaching[i].scene.detach()
        }
    }

    private fun SceneNode.detach() {
        if (this is SceneHub) removeChildren()
        onRemove()
    }

    /**
     * Renders the children ordered before this hub, then the hub itself, then the rest.
     * [visible] gates only this hub's own render, not its children's.
     */
    internal fun renderChildren(draw: Drawer) {
        // Captured once so both halves of the walk agree on the list, even if a child
        // adds or removes a scene from inside its own render.
        val holders = traversalSnapshot

        val index = renderChildrenFor(holders, draw) { it.order < 0}
        if (this is Renderable) {
            if (visible) render(draw) else renderWhileInactive(draw)
        }
        renderChildrenFor(holders, draw, index) { it.order >= 0 }
    }

    private inline fun renderChildrenFor(
        holders: Array<SceneNodeHolder>,
        draw: Drawer,
        from: Int = 0,
        condition: (SceneNodeHolder) -> Boolean
    ): Int {
        var i = from
        while (i < holders.size) {
            val holder = holders[i]
            if (!condition(holder)) break

            if (!holder.detached) renderNode(holder.scene, draw)
            i++
        }
        return i
    }

    private fun renderNode(scene: SceneNode, draw: Drawer) {
        if (!scene.active) {
            scene.renderWhileInactive(draw)
            return
        }

        // A hub renders itself from inside renderChildren, so that its own render lands
        // between the children ordered before it and those ordered after it.
        if (scene is SceneHub) scene.renderChildren(draw)
        else if (scene is Renderable) {
            if (scene.visible) scene.render(draw) else scene.renderWhileInactive(draw)
        }
    }

    /**
     * Updates the children ordered before this hub, then the hub itself, then the rest.
     * [playing] gates only this hub's own update, not its children's.
     */
    internal fun updateChildren(delta: Double) {
        // Captured once so both halves of the walk agree on the list, even if a child
        // adds or removes a scene from inside its own update.
        val holders = traversalSnapshot

        val index = updateChildrenFor(holders, delta) { it.order < 0 }
        if (this is Updateable) {
            if (playing) update(delta) else updateWhileInactive(delta)
        }
        updateChildrenFor(holders, delta, index) { it.order >= 0 }
    }

    private inline fun updateChildrenFor(
        holders: Array<SceneNodeHolder>,
        delta: Double,
        from: Int = 0,
        condition: (SceneNodeHolder) -> Boolean
    ): Int {
        var i = from
        while (i < holders.size) {
            val holder = holders[i]
            if (!condition(holder)) break

            if (!holder.detached) updateNode(holder.scene, delta)
            i++
        }
        return i
    }

    private fun updateNode(scene: SceneNode, delta: Double) {
        if (!scene.active) {
            scene.updateWhileInactive(delta)
            return
        }

        // A hub updates itself from inside updateChildren, so that its own update lands
        // between the children ordered before it and those ordered after it.
        if (scene is SceneHub) scene.updateChildren(delta)
        else if (scene is Updateable) {
            if (scene.playing) scene.update(delta) else scene.updateWhileInactive(delta)
        }
    }

    private class SceneNodeHolder(val scene: SceneNode, val order: Int) {
        /** Set when the node leaves this hub, so a traversal already in flight skips it. */
        var detached = false
    }

}
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

    val hasChildren: Boolean
        get() = childHolders.isNotEmpty()

    fun <S:SceneNode> addScene(scene: S, order: Int = 0): S {
        if (!hasScene(scene)) {
            childHolders.add(SceneNodeHolder(scene, order))
            childHolders.sortBy { it.order }
            listHasChanged = true
        } else {
            throw IllegalArgumentException("Cannot add scene because it has already been added")
        }
        return scene
    }

    /**
     * Detaches [scene] from this hub and notifies its entire subtree, deepest first.
     * Every detached node receives [SceneNode.onRemove] exactly once.
     */
    fun removeScene(scene: SceneNode): Boolean {
        val index = childHolders.indexOfFirst { it.scene === scene }
        if (index == -1) return false

        childHolders.removeAt(index)
        listHasChanged = true
        scene.detach()
        return true
    }

    fun <S : SceneNode> replaceScene(old: S, new: S): S {
        val index = childHolders.indexOfFirst { it.scene === old }
        if (index != -1) {
            val order = childHolders[index].order
            childHolders.removeAt(index)

            childHolders.add(index, SceneNodeHolder(new, order))
            childHolders.sortBy { it.order }
            listHasChanged = true

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
        listHasChanged = true

        for (i in detaching.indices) {
            detaching[i].scene.detach()
        }
    }

    private fun SceneNode.detach() {
        if (this is SceneHub) removeChildren()
        onRemove()
    }

    internal fun renderChildren(draw: Drawer) {
        val index = renderChildrenFor(draw) { it.order < 0}
        if (this is Renderable) {
            this.render(draw)
        }
        renderChildrenFor(draw, index) { it.order >= 0 }
    }

    private inline fun renderChildrenFor(draw: Drawer, from: Int = 0, condition: (SceneNodeHolder) -> Boolean): Int {
        var i = from
        do {
            val holder = childHolders.getOrNull(i) ?: break
            val scene = holder.scene
            if (!condition(holder)) break

            if (scene.active && scene.visible) {
                if (scene is SceneHub) scene.renderChildren(draw)
                else if (scene is Renderable) scene.render(draw)
            }
            i++
        } while (true)
        return i
    }

    internal fun updateChildren(delta: Double) {
        val index = updateChildrenFor(delta) { it.order < 0 }
        if (this is Updateable) {
            this.update(delta)
        }
        updateChildrenFor(delta, index) { it.order >= 0 }
    }

    private inline fun updateChildrenFor(delta: Double, from: Int = 0, condition: (SceneNodeHolder) -> Boolean): Int {
        var inverseI = childHolders.lastIndex - from
        do {
            val holder = childHolders.getOrNull(childHolders.lastIndex - (inverseI)) ?: break
            val scene = holder.scene
            if (condition(holder)) {
                if (scene.active && scene.playing) {
                    if (scene is SceneHub) scene.updateChildren(delta)
                    else if (scene is Updateable) scene.update(delta)
                } else {
                    scene.whileInactive(delta)
                }
            } else break
            inverseI--
        } while (true)
        return childHolders.lastIndex - inverseI
    }


    private class SceneNodeHolder(val scene: SceneNode, val order: Int)

}
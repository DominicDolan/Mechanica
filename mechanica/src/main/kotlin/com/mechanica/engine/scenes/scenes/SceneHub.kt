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

    private val activationListener = ActivationListener()
    final override var active: Boolean by activationListener

    init {
        addActivationChangedListener(1) {
            if (it) onActivate()
            else onDeactivate()
        }
    }
    /**
     * Adds a callback for when the value of [active] is changed. The [priority] value dictates the order in which
     * the callbacks are executed, the higher the priority value, the earlier the callback will execute.
     * The value can be negative, in which case the callback will be called after the value for [active]
     * has been set
     *
     * @param priority an integer value which represents the order that the callbacks are executed, the higher
     * the value, the earlier the execution, the value for [active] is changed at a priority of zero
     *
     * @param listener the callback which will execute when the value of [active] has been changed, the lambda takes
     * a boolean expression which is the new value for [active]
     */
    fun addActivationChangedListener(priority: Int = 0, listener: (Boolean) -> Unit) {
        activationListener.addListener(priority, listener)
    }

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

    fun removeScene(scene: SceneNode): Boolean {
        val sceneToRemove = childHolders.find { it.scene === scene }
        return if (sceneToRemove != null) {
            listHasChanged = true
            childHolders.remove(sceneToRemove)
        } else false
    }

    fun <S : SceneNode> replaceScene(old: S, new: S): S {
        val index = childHolders.indexOfFirst { it.scene === old }
        if (index != -1) {
            val order = childHolders[index].order
            childHolders.removeAt(index)
            childHolders.add(index, SceneNodeHolder(new, order))
            childHolders.sortBy { it.order }
            listHasChanged = true
            return new
        }
        return old
    }

    fun hasScene(scene: SceneNode): Boolean {
        return childHolders.find { it.scene === scene } != null
    }

    internal fun removeChildren() {
        for (i in childHolders.indices) {
            val scene = childHolders[i].scene
            if (scene is SceneHub) scene.removeChildren()
            scene.onRemove()
        }
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

            if (scene.active) {
                if (scene is SceneHub) scene.renderChildren(draw)
                else if (scene is Renderable) scene.render(draw)
            }
            i++
        } while (true)
        return i
    }

    internal fun updateChildren(delta: Double) {
        activationListener.runOnce()

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
                if (scene.active) {
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
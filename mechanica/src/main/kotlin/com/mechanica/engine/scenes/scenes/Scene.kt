package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.processes.Process

abstract class Scene(order: Int = 0) : Process(order), SceneNode {

    protected val childScenes: List<SceneNode> = ArrayList()

    protected val Drawer.inScene: Drawer
        get() = drawInScene(this, view)

    protected open fun drawInScene(draw: Drawer, view: View): Drawer = draw.transformed.translate(view.x, view.y)

    final override fun <S:SceneNode> addScene(scene: S): S {
        addProcess(scene)
        val scenes = (childScenes as ArrayList)

        if (!scenes.contains(scene)) {
            scenes.add(scene)
            scenes.sortBy { it.order }
        }
        return scene
    }

    final override fun removeScene(scene: SceneNode): Boolean {
        removeProcess(scene)
        return (childScenes as ArrayList).remove(scene)
    }

    final override fun <S : SceneNode> replaceScene(old: S, new: S): S {
        replaceProcess(old, new)

        val scenes = (childScenes as ArrayList)
        val index = scenes.indexOf(old)
        if (index != -1) {
            scenes.removeAt(index)
            scenes.add(index, new)
            scenes.sortBy { it.order }
            return new
        }
        return old
    }

    inline fun forEachScene(operation: (SceneNode)-> Unit) {
        for (i in `access$childScenes`.indices) {
            operation(`access$childScenes`[i])
        }
    }

    override fun renderNodes(draw: Drawer) {
        val index = renderNodesFor(draw) { it.order < 0}
        this.render(draw)
        renderNodesFor(draw, index) { it.order >= 0 }
    }

    private inline fun renderNodesFor(draw: Drawer, from: Int = 0, condition: (SceneNode) -> Boolean): Int {
        var i = from
        do {
            val scene = childScenes.getOrNull(i++) ?: break
            if (scene.active) {
                scene.renderNodes(draw)
            }
        } while (condition(scene))
        return i
    }

    override fun update(delta: Double) { }

    override fun destructNodes() {
        for (i in childScenes.indices) {
            childScenes[i].destructNodes()
        }
        super.destructNodes()
    }

    override fun render(draw: Drawer) { }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childScenes`: List<SceneNode>
        get() = childScenes

}
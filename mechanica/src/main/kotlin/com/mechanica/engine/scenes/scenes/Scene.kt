package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.processes.Process

abstract class Scene : Process(), SceneNode {

    protected val childScenes: List<SceneNode> = ArrayList()

    protected open val Drawer.inScene: Drawer
        get() = drawInScene(this, view)

    override fun <S:SceneNode> addScene(scene: S): S {
        (childScenes as ArrayList).add(scene)
        return scene
    }

    override fun removeScene(scene: SceneNode): Boolean {
        scene.destructor()
        return (childScenes as ArrayList).remove(scene)
    }

    override fun <S : SceneNode> replaceScene(old: S, new: S): S {
        val index = childScenes.indexOf(old)
        if (index != -1) {
            childScenes[index].destructor()
            (childScenes as ArrayList)[index] = new
            return new
        }
        return old
    }

    inline fun forEachScene(operation: (SceneNode)-> Unit) {
        for (i in `access$childScenes`.indices) {
            operation(`access$childScenes`[i])
        }
    }

    override fun updateNodes(delta: Double) {
        super.updateNodes(delta)
        for (i in childScenes.indices) {
            childScenes[i].updateNodes(delta)
        }
    }

    override fun renderNodes(draw: Drawer) {
        this.render(draw)
        for (i in childScenes.indices) {
            childScenes[i].renderNodes(draw)
        }
    }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childScenes`: List<SceneNode>
        get() = childScenes

    companion object {
        internal fun drawInScene(draw: Drawer, view: View): Drawer = draw.transformed.translate(view.x, view.y).layout.origin()
    }
}
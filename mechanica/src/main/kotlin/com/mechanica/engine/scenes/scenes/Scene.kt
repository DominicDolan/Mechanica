package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap
import com.mechanica.engine.scenes.processes.Process

abstract class Scene(priority: Int = 0) : Process(priority), SceneNode {

    protected val childScenes: List<SceneNode> = ArrayList()

    protected val Drawer.inScene: Drawer
        get() = drawInScene(this, view)

    protected open fun drawInScene(draw: Drawer, view: View): Drawer = draw.transformed.translate(view.x, view.y)

    final override fun <S:SceneNode> addScene(scene: S): S {
        addProcess(scene)
        val scenes = (childScenes as ArrayList)

        if (!scenes.contains(scene)) {
            scenes.add(scene)
            scenes.sortBy { it.priority }
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
            scenes.sortBy { it.priority }
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
        val index = renderNodesFor(draw) { it.priority < 0}
        this.render(draw)
        renderNodesFor(draw, index) { it.priority >= 0}
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

    /**
     * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.processes.Process.exclusivelyActiveProcesses] but it adds
     * scenes to the parent instead of processes
     *
     * @param scenes the set of scenes that will be added as children to this scene and only one can be
     *                  set to active at a time
     * @return An ExclusiveProcessMap which can have more processes added at a later stage
     */
    protected fun <P: Scene> exclusivelyActiveScenes(vararg scenes: P): ExclusiveActivationMap<P> {
        return ExclusiveSceneMap(*scenes)
    }

    private inner class ExclusiveSceneMap<P: Scene>(vararg scenes: P) : ExclusiveActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            this@Scene.addScene(process)
            return super.addProcess(process)
        }
    }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childScenes`: List<SceneNode>
        get() = childScenes

}
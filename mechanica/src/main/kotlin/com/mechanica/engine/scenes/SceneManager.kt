package com.mechanica.engine.scenes

import com.mechanica.engine.debug.DebugDrawer
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.delta.Updater
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.SceneNode
import com.mechanica.engine.util.Timer

internal class SceneManager(
        private val deltaCalculator: DeltaCalculator,
        private val sceneStarter: (() -> Scene?)) : SceneNode, Updater {

    private val scenes = ChildScenes()

    var updateVar: ((Double) -> Unit)? = null

    private var sceneSetter: () -> SceneNode? = { null }
    var currentScene: SceneNode? = null
        private set(value) {
            val scene = currentScene

            if (value == null) {
                if (scene != null) scenes.removeScene(scene)
                field = null
                return
            }

            if (scene == null) {
                scenes.addScene(value)
                field = value
                return
            }

            val new = scenes.replaceScene(scene, value)
            if (new === scene) {
                scenes.addScene(value)
            }

            field = new
        }

    private var scheduleSceneChange = true

    private var drawer: Drawer? = null

    private var startOfLoop = Timer.now
    private var updateDuration = 0.1

    private var pause = false
    private var hasPaused = false

    private var frameAdvance = false

    fun pauseExecution(pause: Boolean) {
        this.pause = pause
        hasPaused = false

        if (!pause) {
            updateDuration = 0.017
            startOfLoop = Timer.now
        }
    }

    fun frameAdvance() {
        frameAdvance = true
    }

    fun startScene() {
        setMainScene { sceneStarter.invoke() }
    }

    fun setMainScene(setter: () -> SceneNode?) {
        sceneSetter = setter
        scheduleSceneChange = true
    }

    fun addScene(scene: SceneNode, order: Int = 0) = scenes.addScene(scene, order)
    fun removeScene(scene: SceneNode) = scenes.removeScene(scene)

    fun updateAndRender() {
        val now = Timer.now
        val lastFrame = startOfLoop
        startOfLoop = now
        deltaCalculator.updateAndRender(lastFrame, now, this)

        checkStateChange()
    }

    override fun update(delta: Double) {
        if (!pause || frameAdvance) {
            updateVar?.invoke(delta)
            scenes.updateChildren(delta)
            frameAdvance = false
        } else {
            updatePaused()
        }

    }

    override fun render() {
        if (currentScene != null || scenes.hasChildren) {
            scenes.renderChildren(getDrawer())
        }

        if (Game.debug.screenLog && ScreenLog.hasSomethingToRender)
            ScreenLog.render(getDrawer())
        if (Game.debug.constructionDraws && DebugDrawer.hasSomethingToRender)
            DebugDrawer.render(getDrawer())
    }

    private fun checkStateChange() {
        if (scheduleSceneChange) {
            (currentScene as? SceneHub)?.removeChildren()
            currentScene = sceneSetter()
            scheduleSceneChange = false
        }
    }

    private fun getDrawer(): Drawer {
        val drawer = this.drawer ?: Drawer.create()
        this.drawer = drawer
        return drawer
    }


    private fun updatePaused() {
        if (!hasPaused) {
            hasPaused = true
            updateDuration = Timer.now - startOfLoop
        }
        startOfLoop = Timer.now
    }

    override fun onRemove() = scenes.onRemove()

    class ChildScenes : SceneHub()

}
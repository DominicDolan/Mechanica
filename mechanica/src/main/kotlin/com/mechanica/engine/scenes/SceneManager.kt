package com.mechanica.engine.scenes

import com.mechanica.engine.debug.DebugDrawer
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.ConfigurationData
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.delta.Updater
import com.mechanica.engine.game.view.Camera
import com.mechanica.engine.scenes.processes.Updateable
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneNode
import com.mechanica.engine.util.Timer

internal class SceneManager(
        private val data: ConfigurationData) : SceneNode, Updater {

    private val scenes = ChildScenes()

    var updateVar: ((Double) -> Unit)? = null

    private var sceneSetter: () -> Scene? = { null }
    var currentScene: Scene? = null
        private set(value) {
            val scene = currentScene

            if (value == null) {
                if (scene != null) removeScene(scene)
                field = null
                return
            }

            if (scene == null) {
                addScene(value)
                field = value
                return
            }

            val new = replaceScene(scene, value)
            if (new === scene) {
                addScene(value)
            }

            field = new
        }

    private var scheduleSceneChange = true

    private var drawer: Drawer? = null

    private var startOfLoop = Timer.now
    private var updateDuration = 0.1

    private var pause = false
    private var hasPaused = false
    private var pausedUpdate = 0.17

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

    fun setStartingScene(data: GameSetup) {
        val state = data.startingScene

        setMainScene { state?.invoke() }
    }

    fun setMainScene(setter: () -> Scene?) {
        sceneSetter = setter
        scheduleSceneChange = true
    }

    fun updateAndRender() {
        val now = Timer.now
        val lastFrame = startOfLoop
        startOfLoop = now
        data.deltaCalculator?.updateAndRender(lastFrame, now, this)

        checkStateChange()
    }

    override fun update(delta: Double) {
        if (!pause || frameAdvance) {
            updateVar?.invoke(delta)
            scenes.updateNodes(delta)
            frameAdvance = false
        } else {
            updatePaused()
        }

    }

    override fun render() {
        if (currentScene != null || scenes.hasChildren) {
            scenes.renderNodes(getDrawer())
        }

        if (Game.debug.screenLog)
            ScreenLog.render(getDrawer())
        if (Game.debug.constructionDraws)
            DebugDrawer.render(getDrawer())
    }

    private fun checkStateChange() {
        if (scheduleSceneChange) {
            currentScene?.destructNodes()
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

    override val camera: Camera
        get() = currentScene?.camera ?: Game.world
    override fun <S : SceneNode> addScene(scene: S): S = scenes.addScene(scene)
    override fun removeScene(scene: SceneNode) = scenes.removeScene(scene)
    override fun <S : SceneNode> replaceScene(old: S, new: S): S = scenes.replaceScene(old, new)
    override fun renderNodes(draw: Drawer) = scenes.renderNodes(draw)
    override fun <P : Updateable> addProcess(process: P): P = scenes.addProcess(process)
    override fun removeProcess(process: Updateable): Boolean = scenes.removeProcess(process)
    override fun <P : Updateable> replaceProcess(old: P, new: P): P = scenes.replaceProcess(old, new)
    override fun updateNodes(delta: Double) = scenes.updateNodes(delta)
    override fun destructor() = scenes.destructor()
    override fun destructNodes() = scenes.destructNodes()
    override fun hasScene(scene: SceneNode) = scenes.hasScene(scene)

    override fun render(draw: Drawer) { }

    class ChildScenes : Scene() {
        val hasChildren: Boolean
            get() = childScenes.isNotEmpty()
    }

}
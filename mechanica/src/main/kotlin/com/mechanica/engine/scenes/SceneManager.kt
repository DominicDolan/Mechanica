package com.mechanica.engine.scenes

import com.mechanica.engine.debug.DebugDrawer
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.ConfigurationData
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.Timer

internal class SceneManager(private val data: ConfigurationData) : Scene() {
    override val view: View
        get() = Game.view

    var updateVar: ((Double) -> Unit)? = null

    private var sceneSetter: () -> MainScene? = { null }
    var currentScene: MainScene? = null
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

    fun setMainScene(setter: () -> MainScene?) {
        sceneSetter = setter
        scheduleSceneChange = true
    }

    fun updateScenes(): Double {
        if (!pause || frameAdvance) {
            updateScene()
            frameAdvance = false
        } else {
            updatePaused()
        }

        render()
        checkStateChange()

        return updateDuration
    }

    private fun updateScene() {
        updateDuration = data.deltaConfiguration?.invoke(startOfLoop, Timer.now) ?: 0.017
        startOfLoop = Timer.now

        updateNodes(updateDuration)
    }

    override fun update(delta: Double) {
        updateVar?.invoke(delta)
    }

    private fun render() {
        if (currentScene != null || childScenes.isNotEmpty()) {
            renderNodes(getDrawer())
        }

        if (Game.debug.screenLog)
            ScreenLog.render(getDrawer())
        if (Game.debug.constructionDraws)
            DebugDrawer.render(getDrawer())
    }

    override fun render(draw: Drawer) { }

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


}
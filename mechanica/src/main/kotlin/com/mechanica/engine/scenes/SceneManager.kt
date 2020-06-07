package com.mechanica.engine.scenes

import com.mechanica.engine.debug.DebugDrawer
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.processes.Process
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.Timer

internal class SceneManager : Scene() {
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

    fun setStartingScene(data: GameSetup) {
        val state = data.startingScene
        val loadState = data.loadState?.invoke()

        if (loadState != null) {
            loadState.onFinish = {
                setMainScene { state?.invoke() }
            }
            setMainScene {
                loadState.preLoad()
                loadState
            }
        } else {
            setMainScene { state?.invoke() }
        }
    }

    fun setMainScene(setter: () -> MainScene?) {
        sceneSetter = setter
        scheduleSceneChange = true
    }

    fun updateScenes(): Double {
        updateDuration = Timer.now - startOfLoop
        startOfLoop = Timer.now

        checkStateChange()

        updateNodes(updateDuration)

        render()

        return updateDuration
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
            currentScene = sceneSetter()
            scheduleSceneChange = false
        }
    }

    private fun getDrawer(): Drawer {
        val drawer = this.drawer ?: Drawer.create()
        this.drawer = drawer
        return drawer
    }

}
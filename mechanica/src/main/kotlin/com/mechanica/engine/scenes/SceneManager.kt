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
        private val maxFrameTime: Double,
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

    private var paused = false
    private var stepsPending = 0

    val isPaused: Boolean
        get() = paused

    fun pauseExecution(pause: Boolean) {
        this.paused = pause
        stepsPending = 0
        startOfLoop = Timer.now
    }

    /**
     * Queues [frames] single steps, each simulating exactly one [DeltaCalculator.timeStep],
     * one per rendered frame. Only has an effect while execution is paused.
     */
    fun step(frames: Int) {
        stepsPending += frames
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
        var lastFrame = startOfLoop
        startOfLoop = now

        // A gap this large is never a slow frame, it's the process having been suspended: a
        // breakpoint, a long GC pause, an OS sleep or a window drag. Simulating it would hand
        // update() a delta orders of magnitude larger than any real frame, which tunnels
        // physics straight through colliders, so drop the time instead of catching up on it.
        if (now - lastFrame > maxFrameTime) {
            lastFrame = now
            deltaCalculator.resync()
        }

        when {
            !paused -> deltaCalculator.updateAndRender(lastFrame, now, this)
            // Every step is the same size regardless of how long we waited between them,
            // so stepping is repeatable and the delta never depends on the debugger.
            stepsPending > 0 -> {
                stepsPending--
                deltaCalculator.step(this, deltaCalculator.timeStep)
            }
            // Frozen, but still drawing, and the loop still pumps input so the debug keys work.
            else -> render()
        }

        checkStateChange()
    }

    override fun update(delta: Double) {
        updateVar?.invoke(delta)
        scenes.updateChildren(delta)
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

    override fun onRemove() = scenes.onRemove()

    class ChildScenes : SceneHub()

}
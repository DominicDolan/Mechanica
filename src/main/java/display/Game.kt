package display

import data.loadData
import data.saveData
import matrices.ProjectionMatrix
import matrices.ViewMatrix
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.glClearColor
import debug.BodyRenderer
import renderer.Painter
import renderer.backRenderer
import renderer.fbo
import state.State

/**
 * Created by domin on 25/10/2017.
 */
object Game {
    internal var debug: Boolean = false
    var ready = false

    internal var displayManager: DisplayManager? = null
    internal var view: WorldView? = null
    internal var saveData: Any = object {}

    private var loop: GameLoop? = null

    private var painter: Painter? = null

    val height: Int
        get() = displayManager?.height?: 0
    val width: Int
        get() = displayManager?.width?: 0
    val ratio: Double
        get() = displayManager?.ratio?: 1.0

    val viewHeight: Double
        get() = view?.height?: 0.0
    val viewWidth: Double
        get() = view?.width?: 0.0

    var viewX: Double
        get() = view?.positionX?: 0.0
        set(value) {view?.positionX = value}
    var viewY: Double
        get() = view?.positionY?: 0.0
        set(value) {view?.positionY = value}

    val viewMatrix: ViewMatrix
        get() = view?.viewMatrix?: ViewMatrix()
    val uiViewMatrix: ViewMatrix
        get() = view?.UIView?: ViewMatrix()
    val projectionMatrix: ProjectionMatrix
        get() = view?.projectionMatrix?: ProjectionMatrix()

    var world: World = World(Vec2(0f, -9.8f))

    var currentState: State
        get() = (loop?: createGameLoop()).currentState
        set(value) {
            (loop?: createGameLoop()).currentState = value
        }

    // The window handle
    private val window: Long
        get() = displayManager?.window?: 0

    private fun emptyState(): State {
        return object : State(){
            override fun init() {}
            override fun update(delta: Float) {}
            override fun render(g: Painter) {}

        }
    }


    fun update(loop: GameLoop = createGameLoop()) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        // Set the clear color
        glClearColor(1.0f, 0.5f, 0.0f, 0.0f)

        // Run the rendering update until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            loop.update()
            glfwSwapBuffers(window) // swap the color loader.getBuffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents()
        }
    }


    private fun createGameLoop(): GameLoop {
        val loop = this.loop
        if (loop != null) return loop
        else {
            this.loop = object : GameLoop() {

                var updateDurationMillis: Long = 0
                var sleepDurationMillis: Long = 0

                override var currentState: State
                    set(value) {
                        System.gc()
                        if (!value.initialised)
                            value.init()
                        value.initialised = true
                        field = value
                    }

                override fun setCurrentState(setState: () -> State) {
                    currentState = setState()
                }

                init {
                    currentState = emptyState()
                }

                override fun update() {
                    val beforeUpdateRender = System.nanoTime()
                    val deltaMillis = sleepDurationMillis + updateDurationMillis

//                    fbo.bindFrameBuffer()
                    val painter = painter
                    if (ready && painter == null){
                        Game.painter = Painter()
                    }
                    if (ready && painter != null) {
                        backRenderer()

                        val del = deltaMillis.toFloat() / 1000f
                        currentState.update(del)
                        world.step(del, 8, 5)
                        world.step(del, 8, 5)
                        currentState.render(painter)
                        if (debug) {
                            BodyRenderer.update(painter)
                            world.drawDebugData()
                        }
                    }

//		            fbo.unbindFrameBuffer()
//                    fbo.doPostProcessing()

                    updateDurationMillis = (System.nanoTime() - beforeUpdateRender) / 1000000L
                    sleepDurationMillis = Math.max(2, 17 - updateDurationMillis)

                    try {
                        Thread.sleep(sleepDurationMillis)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            }

            return this.loop ?: createGameLoop()
        }
    }


    fun destroy(){
        save()
        displayManager?.destroy()
    }

    fun close() {
        glfwSetWindowShouldClose(window, true) // We will detect this in the rendering update
    }

    fun save() {
        saveData(saveData)
    }

    fun load() {
        loadData(saveData)
    }

    abstract class GameLoop {
        abstract var currentState: State

        abstract fun setCurrentState(setState: () -> State)

        abstract fun update()
    }




}

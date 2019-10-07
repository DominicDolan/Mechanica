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
import input.ControlsMap
import physics.ContactEvents
import renderer.Painter
import renderer.backRenderer
import state.EmptyState
import state.EmptyLoadState
import state.LoadState
import state.State
import util.FrameQueue
import util.Timer
import java.util.*

/**
 * Created by domin on 25/10/2017.
 */
object Game {
    internal var debug: Boolean = false
    var ready = false

    internal var displayManager: DisplayManager? = null
    internal var view: WorldView? = null
    internal var saveData: Any? = null
    internal var controls: ControlsMap = object : ControlsMap() { }

    private var loop: GameLoop? = null

    private var painter: Painter? = null

    val height: Int
        get() = displayManager?.height?: 0
    val width: Int
        get() = displayManager?.width?: 0
    val ratio: Double
        get() = displayManager?.ratio?: 1.0

    var viewHeight: Double
        get() = view?.height?: 0.0
        set(value) { view?.height = value}
    var viewWidth: Double
        get() = view?.width?: 0.0
        set(value) { view?.width = value}

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

//    var currentState: State
//        get() = (loop?: createGameLoop()).currentState
//        private set(value) {
//            (loop?: createGameLoop()).currentState = value
//        }

    fun setCurrentState(setter: () -> State) {
        (loop?: createGameLoop()).setCurrentState(setter)
    }

    // The window handle
    private val window: Long
        get() = displayManager?.window?: 0

    private fun emptyState(): State {
        return object : State(){
            override fun update(delta: Double) {}
            override fun render(g: Painter) {}

        }
    }

    fun start(options: GameOptions) {
        with(options) {
            displayManager = DisplayManager(resolutionWidth, resolutionHeight, fullscreen, borderless)
            view = getView()
            Game.saveData = this.saveData
            loadData()
            controls = controlsMap
            world = World(gravity)
            world.setContactListener(ContactEvents)
            Game.debug = debug
            if (debug) {
                BodyRenderer.init()
            }

            val state = startingState ?: { EmptyState }
            val loadState = this.loadState ?: EmptyLoadState
            loadState.startingState = state

            setCurrentState {
                loadState.preLoad()
                loadState
            }
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

        // Just mentioning the Timer object will initialize it here
        Timer

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
                var startOfLoop = Timer.now
                var endOfLoop = Timer.now - 0.1
                var updateDuration = startOfLoop - endOfLoop

                private val frameQueue = FrameQueue(60, 17.0/1000.0)

                override fun setCurrentState(setter: () -> State) {
                    System.gc()
                    currentState = setter()
                }

                private lateinit var currentState: State

                init {
                    setCurrentState { emptyState() }
                }

                override fun update() {
                    updateDuration = Timer.now - startOfLoop
                    startOfLoop = Timer.now
                    if (currentState !is LoadState) {
                        frameQueue.add(updateDuration)
                    }

                    val painter = painter
                    if (ready && painter == null){
                        Game.painter = Painter()
                    }
                    if (ready && painter != null) {
                        backRenderer()

                        val del = updateDuration
                        currentState.update(del)

                        val average = frameQueue.average.toFloat()
                        // world.step is called twice because it tends to resolve contacts in one frame instead of two
                        // which makes it easier to deal with
                        world.step(average/2.0f, 8, 5)
                        world.step(average/2.0f, 8, 5)

                        currentState.render(painter)
                        if (debug) {
                            BodyRenderer.update(painter)
                            world.drawDebugData()
                        }
                    }
                }

                private fun Queue<Double>.average(): Double {
                    var total = 0.0
                    this.forEach { total += it }
                    return total/this.size
                }

            }

            return this.loop ?: createGameLoop()
        }
    }


    fun destroy(){
        saveData()
        displayManager?.destroy()
    }

    fun close() {
        glfwSetWindowShouldClose(window, true) // We will detect this in the rendering update
    }

    fun saveData() {
        val saveData = saveData
        if (saveData != null) {
            saveData(saveData)
        }
    }

    fun loadData() {
        val saveData = saveData
        if (saveData != null) {
            loadData(saveData)
        }
    }

    abstract class GameLoop {
        abstract fun setCurrentState(setter: () -> State)

        abstract fun update()
    }




}

package display

import data.loadData
import data.saveData
import debug.BodyRenderer
import debug.DebugDrawer
import graphics.backRenderer
import graphics.drawer.Drawer
import input.ControlsMap
import matrices.ProjectionMatrix
import matrices.ViewMatrix
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.glClearColor
import physics.ContactEvents
import state.EmptyLoadState
import state.EmptyState
import state.LoadState
import state.State
import util.FrameQueue
import util.Timer
import util.Updateable

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

    private var painter: Drawer? = null

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
        set(value) {
            view?.positionY = value
        }

    val viewMatrix: ViewMatrix
        get() = view?.viewMatrix?: ViewMatrix()
    val uiViewMatrix: ViewMatrix
        get() = view?.uiView?: ViewMatrix()
    val projectionMatrix: ProjectionMatrix
        get() = view?.projectionMatrix?: ProjectionMatrix()

    var world: World = World(Vec2(0f, -9.8f))

//    var currentState: State
//        get() = (loop?: createGameLoop()).currentState
//        private set(value) {
//            (loop?: createGameLoop()).currentState = value
//        }

    fun setCurrentState(setter: () -> State) {
        getGameLoopInstance().setCurrentState(setter)
    }

    internal fun addItemToUpdate(updateable: Updateable) {
        getGameLoopInstance().updateableManager.addUpdateable(updateable)
    }

    internal fun stopItemUpdating(updateable: Updateable) {
        getGameLoopInstance().updateableManager.removeUpdateable(updateable)
    }

    internal fun addItemToUpdateForCurrentState(updateable: Updateable) {
        getGameLoopInstance().updateableManager.addCurrentStateUpdateable(updateable)
    }

    // The window handle
    private val window: Long
        get() = displayManager?.window?: 0

    private fun emptyState(): State {
        return object : State(){
            override fun update(delta: Double) {}
            override fun render(draw: Drawer) {}

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


    fun update() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        // Set the clear color
        glClearColor(1.0f, 0.5f, 0.0f, 0.0f)

        // Just mentioning the Timer object will initialize it here
        Timer

        val loop = getGameLoopInstance()

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


    private fun getGameLoopInstance(): GameLoop {
        val loop = this.loop
        if (loop != null) return loop
        else {
            this.loop = object : GameLoop() {
                var startOfLoop = Timer.now
                var endOfLoop = Timer.now - 0.1
                var updateDuration = startOfLoop - endOfLoop

                private var setStateOnNextFrame = true
                private var setter = { emptyState() }

                private val frameQueue = FrameQueue(60, 17.0/1000.0)

                override val updateableManager: UpdateableManager = UpdateableManager()

                override fun setCurrentState(setter: () -> State) {
                    val setStateNow = setStateOnNextFrame
                    setStateOnNextFrame = true
                    this.setter = setter

                    if (setStateNow) {
                        setCurrentState()
                    }
                }

                private fun setCurrentState() {
                    updateableManager.removeStateUpdateables()
                    currentState = setter()
                    setStateOnNextFrame = false
                    System.gc()
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
                        Game.painter = Drawer()
                    }
                    if (ready && painter != null) {
                        backRenderer()

                        val del = updateDuration
                        currentState.update(del)

                        updateableManager.update(del)

                        val average = frameQueue.average.toFloat()
                        // world.step is called twice because it tends to resolve contacts in one frame instead of two
                        // which makes it easier to deal with
                        world.step(average/2.0f, 8, 5)
                        world.step(average/2.0f, 8, 5)

                        currentState.render(painter)
                        if (debug) {
                            BodyRenderer.update(painter)
                            world.drawDebugData()
                            DebugDrawer.render(painter)
                        }

                        if (setStateOnNextFrame) {
                            setCurrentState()
                        }
                    }
                }
            }

            return this.loop ?: throw IllegalStateException("Something went wrong when creating the game loop")
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

    private class UpdateableManager {

        private val updateables = ArrayList<Updateable>()
        private val stateUpdateables = ArrayList<Updateable>()

        fun update(delta: Double) {
            for (updateable in updateables) {
                updateable.update(delta)
            }
        }

        fun addUpdateable(updateable: Updateable) {
            updateables.add(updateable)
        }

        fun addCurrentStateUpdateable(updateable: Updateable) {
            stateUpdateables.add(updateable)
            updateables.add(updateable)
        }

        fun removeUpdateable(updateable: Updateable) {
            updateables.remove(updateable)
        }

        fun removeStateUpdateables() {
            updateables.removeAll(stateUpdateables)
            stateUpdateables.clear()
        }
    }

    private abstract class GameLoop {
        abstract val updateableManager: UpdateableManager

        abstract fun setCurrentState(setter: () -> State)

        abstract fun update()
    }




}

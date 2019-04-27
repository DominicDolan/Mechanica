package display

import matrices.ProjectionMatrix
import matrices.ViewMatrix
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import renderer.Painter
import state.LoadState
import state.State

/**
 * Created by domin on 28/10/2017.
 */
class GameBuilder {
    private var startingState: State? = null
    private var loadState: LoadState? = null
    private var saveData: Any? = null

    private var viewWidth: Double = 0.0
    private var viewHeight: Double = 0.0
    private var resolutionWidth: Int = 0
    private var resolutionHeight: Int = 0
    private var fullscreen: Boolean = false
    private var borderless: Boolean = false
    private var viewPositionX: Double = 0.0
    private var viewPositionY: Double = 0.0
    private var gravity = Vec2(0f, -9.8f)
    private var debug = false


    fun setResolution(width: Int, height: Int): GameBuilder{
        resolutionWidth = width
        resolutionHeight = height
        return this
    }

    fun setFullscreen(fullscreen: Boolean, borderless: Boolean): GameBuilder {
        this.fullscreen = fullscreen
        this.borderless = borderless
        return this
    }

    fun setStartingState(state: State): GameBuilder {
        startingState = state
        return this
    }


    fun setViewPort(width: Double = 0.0, height: Double = 0.0): GameBuilder{
        viewWidth = width
        viewHeight = height
        return this
    }

    fun setPhysics(gravity: Vec2): GameBuilder{
        this.gravity = gravity

        return this
    }

    fun setViewLocation(positionX: Double = 0.0, positionY: Double = 0.0): GameBuilder{
        viewPositionX = positionX
        viewPositionY = positionY
        return this
    }

    fun setLoader(loader: LoadState): GameBuilder{
        loadState = loader
        return this
    }

    fun setSaveData(saveData: Any): GameBuilder{
        this.saveData = saveData
        return this
    }

    fun setDebugMode(debug: Boolean): GameBuilder {
        this.debug = debug
        return this
    }

    fun start(){
        Game.displayManager = DisplayManager(resolutionWidth, resolutionHeight, fullscreen, borderless)
        Game.view = View(viewWidth, viewHeight, viewPositionX, viewPositionY)
        Game.world = World(gravity)
        Game.debug = debug

        val state = startingState?:emptyState()
        val loadState = this.loadState?:emptyLoadState()
        loadState.startingState = state

        Game.currentState = loadState
    }

    private fun emptyState(): State {
        return object : State(){
            override fun init() {}
            override fun update(delta: Float) {}
            override fun render(g: Painter) {}

        }
    }

    private fun emptyLoadState(): LoadState {
        return object : LoadState(){
            override fun preLoad() {}
            override fun renderLoadScreen(g: Painter) {}
            override fun load() {}
        }
    }

    private class View(width: Double = 0.0, height: Double = 0.0, positionX: Double = 0.0, positionY: Double = 0.0) : WorldView(){
        private var privateWidth: Double = 0.0
        private var privateHeight: Double = 0.0


        override var width: Double
            get() = privateWidth
            set(value) {
                setPort(value, 0.0)
                setViewMatrix()
            }
        override var height: Double
            get() = privateHeight
            set(value) {
                setPort(0.0, value)
                setViewMatrix()
            }

        override var positionX: Double = positionX
            set(value) {
                field = value
                setViewMatrix()
            }
        override var positionY: Double = positionY
            set(value) {
                field = value
                setViewMatrix()
            }

        override val projectionMatrix: ProjectionMatrix
        override val viewMatrix: ViewMatrix
        override val UIView: ViewMatrix

        init {
            setPort(width, height)

            viewMatrix = ViewMatrix()
            UIView = ViewMatrix()
            projectionMatrix = ProjectionMatrix()

            setViewMatrix()
            setUIViewMatrix()
            ////        Units.init()
            //        //        State.updateUnits();
            //        //        Painter.updateUnits();
        }

        private fun setPort(width: Double, height: Double){
            if ((width == 0.0)&&(height == 0.0)){
                privateHeight = Game.height.toDouble()
                privateWidth = Game.width.toDouble()
            }else if ((width == 0.0)||(height == 0.0)) {
                if (height == 0.0) {
                    privateWidth = width
                    privateHeight = width/ Game.ratio
                } else {
                    privateHeight = height
                    privateWidth = height* Game.ratio
                }
            } else {
                privateWidth = width
                privateHeight = height
            }
        }

        private fun setViewMatrix(){

            //View Matrix:
            val fov = Math.toRadians(projectionMatrix.fov.toDouble())

            //δ = 2*atan(d/2D)
            //δ is angular diameter, 70 degrees.
            //d is actual size, 10
            //D is distance away. ?
            //d/2*tan(δ/2) = D

            val cameraZ = width / (2 * Math.tan(fov / 2))
            viewMatrix.setTranslate(positionX, positionY, cameraZ)


        }

        private fun setUIViewMatrix(){
            //UIMatrix
            val fov = Math.toRadians(projectionMatrix.fov.toDouble())

            //δ = 2*atan(d/2D)
            //δ is angular diameter, 70 degrees.
            //d is actual size, 10
            //D is distance away. ?
            //d/2*tan(δ/2) = D

            val cameraZUI = width/ (2 * Math.tan(fov / 2))

            UIView.setTranslate(0.0, 0.0, cameraZUI)
        }

    }
}
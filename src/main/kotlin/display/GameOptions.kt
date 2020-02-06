@file:Suppress("unused") // There will be many functions here that go unused most of the time
package display

import matrices.ProjectionMatrix
import input.ControlsMap
import matrices.ViewMatrix
import org.jbox2d.common.Vec2
import state.LoadState
import state.State
import kotlin.math.tan

/**
 * Created by domin on 28/10/2017.
 */
class GameOptions {
    internal var startingState: (() -> State)? = null
        private set
    internal var loadState: LoadState? = null
        private set
    internal var saveData: Any? = null
        private set
    internal var controlsMap: ControlsMap = object : ControlsMap() { }

    internal var title: String = "Hello, World"
        private set

    private var viewWidth: Double = 0.0
    private var viewHeight: Double = 0.0
    private var viewPositionX: Double = 0.0
    private var viewPositionY: Double = 0.0

    internal var resolutionWidth: Int = 0
        private set
    internal var resolutionHeight: Int = 0
        private set
    internal var fullscreen: Boolean = false
        private set
    internal var borderless: Boolean = false
        private set
    internal var gravity = Vec2(0f, -9.8f)
        private set
    internal var debug = false
        private set


    fun setResolution(width: Int, height: Int): GameOptions{
        resolutionWidth = width
        resolutionHeight = height
        return this
    }

    fun setFullscreen(fullscreen: Boolean, borderless: Boolean): GameOptions {
        this.fullscreen = fullscreen
        this.borderless = borderless
        return this
    }

    fun setStartingState(state: () -> State): GameOptions {
        startingState = state
        return this
    }


    fun setViewPort(width: Double = 0.0, height: Double = 0.0): GameOptions{
        viewWidth = width
        viewHeight = height
        return this
    }

    fun setPhysics(gravity: Vec2): GameOptions{
        this.gravity = gravity

        return this
    }

    fun setViewLocation(positionX: Double = 0.0, positionY: Double = 0.0): GameOptions{
        viewPositionX = positionX
        viewPositionY = positionY
        return this
    }

    fun setLoader(loader: LoadState): GameOptions{
        loadState = loader
        return this
    }

    fun setSaveData(saveData: Any): GameOptions{
        this.saveData = saveData
        return this
    }

    fun setDebugMode(debug: Boolean): GameOptions {
        this.debug = debug
        return this
    }

    fun setControlMapping(controlsMap: ControlsMap): GameOptions {
        this.controlsMap = controlsMap
        return this
    }

    fun setTitle(title: String): GameOptions {
        this.title = title
        return this
    }

    fun getView() : WorldView {
        return View(viewWidth, viewHeight, viewPositionX, viewPositionY)
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
        override val uiView: ViewMatrix

        init {
            setPort(width, height)

            viewMatrix = ViewMatrix()
            uiView = ViewMatrix()
            projectionMatrix = ProjectionMatrix()

            setViewMatrix()
            setUIViewMatrix()
            ////        Units.init()
            //        //        State.updateUnits();
            //        //        Painter.updateUnits();
        }

        private fun setPort(width: Double, height: Double){
            if ((width == 0.0)&&(height == 0.0)){
                privateHeight = height
                privateWidth = width
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

            val cameraZ = width / (2 * tan(fov / 2))
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

            val cameraZUI = width/ (2 * tan(fov / 2))

            uiView.setTranslate(0.0, 0.0, cameraZUI)
        }

    }
}
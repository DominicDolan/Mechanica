package game.view

import display.Game
import game.configuration.GameSetup
import matrices.ProjectionMatrix
import matrices.ViewMatrix
import org.joml.Matrix4f
import kotlin.math.tan

internal class GameMatrices(data: GameSetup) : Matrices {
    override val projection: ProjectionMatrix = ProjectionMatrix()
    override val view: ViewMatrix = ViewMatrix()
    override val uiView: ViewMatrix = ViewMatrix()

    val pvMatrix = Matrix4f()
    val pvUiMatrix = Matrix4f()

    init {
        updateView(data.viewX, data.viewY, data.viewWidth)
        uiView.copy(view)
    }

    fun updateView(viewData: GameView) {
        updateView(viewData.x, viewData.y, viewData.width)
    }

    private fun updateView(x: Double, y: Double, width: Double) {
        val fov = Math.toRadians(projection.fov.toDouble())

/*      δ = 2*atan(d/2D)
        δ is angular diameter or fov, 70 degrees.
        d is actual size, 10
        D is distance away. ?
        D = d/(2*tan(δ/2))
*/      val cameraZ = width / (2 * tan(fov / 2))
        view.setTranslate(x, y, cameraZ)
        projection.scheduleCreation = true
    }


    fun updateMatrices() {
        val pvMatrix = Game.viewMatrix.getWithProjection(Game.projectionMatrix)
        val pvUiMatrix = Game.uiViewMatrix.getWithProjection(Game.projectionMatrix)
        this.pvMatrix.set(pvMatrix)
        this.pvUiMatrix.set(pvUiMatrix)
    }
}
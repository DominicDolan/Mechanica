package display

import matrices.ProjectionMatrix
import matrices.ViewMatrix

/**
 * Created by domin on 27/10/2017.
 */
abstract class WorldView (height: Double = 0.0, width: Double = 0.0, positionX: Double = 0.0, positionY: Double = 0.0) {
    abstract var width: Double
    abstract var height: Double
    abstract var positionX: Double
    abstract var positionY: Double
    abstract val projectionMatrix: ProjectionMatrix
    abstract val viewMatrix: ViewMatrix
    abstract val UIView: ViewMatrix

}
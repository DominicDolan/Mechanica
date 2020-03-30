package game.view

import matrices.ProjectionMatrix
import matrices.ViewMatrix

interface Matrices {
    val projection: ProjectionMatrix
    val view: ViewMatrix
    val uiView: ViewMatrix
}
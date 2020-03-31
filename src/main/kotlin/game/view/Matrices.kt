package game.view

import matrices.ProjectionMatrix
import matrices.ViewMatrix
import org.joml.Matrix4f

interface Matrices {
    val projection: Matrix4f
    val view: Matrix4f
    val uiView: Matrix4f
}
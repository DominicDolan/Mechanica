package world

import compatibility.Polygon
import loader.loadTriangulatedModel
import loader.loadUnitQuad
import models.Model
import org.joml.Matrix4f

/**
 * Created by domin on 01/11/2017.
 */
class WorldGraphics : Iterable<Model>{
    private val model = loadUnitQuad()
    private val graphics = ArrayList<Model>()

    fun add(texture: Int, matrix: Matrix4f){
        graphics.add(MatrixModel(texture, matrix))
    }

    fun add(polygon: Polygon){
        graphics.add(loadTriangulatedModel(polygon.toFloatArray()))
    }

    override fun iterator() = graphics.iterator()

    operator fun get(i: Int) = graphics[i]

    override fun toString(): String {
        var str = ""
        graphics.forEach { str += "\n" + it }
        return str
    }

    inner class MatrixModel(texture: Int, val matrix: Matrix4f) : Model(model.vaoID, model.vertexCount, texture)
}
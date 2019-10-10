package world

import compatibility.PolygonConverter
import graphics.Image
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

    fun add(texture: Image, matrix: Matrix4f){
        graphics.add(MatrixModel(texture, matrix))
    }

    fun add(polygonConverter: PolygonConverter){
        graphics.add(loadTriangulatedModel(polygonConverter.toFloatArray()))
    }

    override fun iterator() = graphics.iterator()

    operator fun get(i: Int) = graphics[i]

    override fun toString(): String {
        var str = ""
        graphics.forEach { str += "\n" + it }
        return str
    }

    inner class MatrixModel(texture: Image, val matrix: Matrix4f) : Model(model.vaoID, model.vertexCount, texture)
}
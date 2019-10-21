package compatibility

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import graphics.Polygon
import loader.loadTriangulatedModel
import models.Model
import org.jbox2d.common.Vec2
import svg.SVGPolygon
import util.extensions.centroid
import java.util.*

/**
 * Created by domin on 02/11/2017.
 */
class PolygonConverter(floatArray: FloatArray) {
    private var floatArray = createUnitQuad()
    private var model: Model? = null
    private var vecArray: Array<Vec2>? = null
    private var geometry: Geometry? = null

    init {
        this.floatArray = floatArray
        vecArray = createVecArray(floatArray)
        geometry = createGeometry(floatArray)
        model = createModel(floatArray)
    }

    constructor(vecArray: Array<Vec2>): this(Array(vecArray.size*3, {0f}).toFloatArray()){
        this.vecArray = vecArray
        vecArray.forEachIndexed { i, v ->
            floatArray[3*i] = v.x
            floatArray[3*i+1] = v.y
        }
        geometry = createGeometry(floatArray)
        model = createModel(floatArray)
    }

    constructor(geometry: Geometry): this(Array((geometry.coordinates.size-1)*3, {0f}).toFloatArray()){
        this.geometry = geometry
        val coordinates = geometry.coordinates
        for (i in 0..coordinates.size-2){
            val c = coordinates[i]
            floatArray[3*i] = c.x.toFloat()
            floatArray[3*i+1] = c.y.toFloat()
        }

        vecArray = createVecArray(floatArray)
        model = createModel(floatArray)
    }

    constructor(svgPolygon: SVGPolygon): this(Array(svgPolygon.path.size*3, {0f}).toFloatArray()){
        svgPolygon.path.forEachIndexed { i, v ->
            floatArray[3*i] = v.x.toFloat()
            floatArray[3*i+1] = v.y.toFloat()
        }
        vecArray = createVecArray(floatArray)
        geometry = createGeometry(floatArray)
        model = createModel(floatArray)
    }


    private fun createVecArray(floatArray: FloatArray): Array<Vec2>{
        return Array(floatArray.size/3) { Vec2(floatArray[3*it], floatArray[3*it+1]) }
    }

    private fun createGeometry(floatArray: FloatArray): Geometry{
        val coordinates = Array(floatArray.size/3 + 1) {
            if (3*it < floatArray.size)
                Coordinate(floatArray[3*it].toDouble(), floatArray[3*it+1].toDouble())
            else
                Coordinate(floatArray[0].toDouble(), floatArray[1].toDouble())
        }
        return GeometryFactory().createPolygon(coordinates)
    }

    private fun createModel(floatArray: FloatArray): Model{
        return loadTriangulatedModel(floatArray)
    }

    private fun createUnitQuad(): FloatArray {
        val left = 0f; val top = 1f; val bottom = 0f; val right = 1f
        return floatArrayOf(left, top, 0.0f, left, bottom, 0.0f, right, bottom, 0.0f, right, top, 0.0f)
    }

    override fun toString(): String {
        return "Float Array: ${Arrays.toString(floatArray)}" +
                "\nVec Array: ${Arrays.toString(vecArray)}" +
                "\nGeometry: $geometry"
    }

    fun toVecArray() = vecArray?: createVecArray(floatArray)
    fun toGeometry() = geometry?: createGeometry(floatArray)
    fun toModel() = model?: createModel(floatArray)
    fun toFloatArray() = floatArray
    fun toPolygon() = object : Polygon {
        override val path: List<Vector>
            get() = toVecArray().map { Vector(it) }
        override val centroid: Vector
            get() = path.centroid()
        override val model: Model
            get() = toModel()

        override fun adjust(toOrigin: Boolean, keepAspectRatio: Boolean, width: Double, height: Double) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
package com.mechanica.engine.drawer.state

import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.util.extensions.fori
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.tan

class DrawStateVariableList {
    private val list = ArrayList<Resettable?>()
    var writeMode = true
        private set

    fun addVector3(resetValue: Float = 0f): Vector3Variable {
        val newVariable = Vector3Variable(this, list.size, resetValue)
        list.add(newVariable)
        return newVariable
    }

    fun addVector2(resetValue: Double = 0.0): Vector2Variable {
        return addVector2(DynamicVector.create(), resetValue)
    }

    fun addVector2(vector: DynamicVector, resetValue: Double = 0.0): Vector2Variable {
        val newVariable = Vector2Variable(this, vector, list.size, resetValue)
        list.add(newVariable)
        return newVariable
    }

    fun addDouble(value: Double): DrawStateDouble {
        val newVariable = DrawStateDouble(value, list.size)
        list.add(newVariable)
        return newVariable
    }

    fun <T> add(variable: T, resetter: (T)->Unit): DrawStateVariable<T> {
        val newVariable = object : DrawStateVariable<T>(this, variable, list.size) {
            override fun reset() {
                resetter(this.variable)
            }
        }
        list.add(newVariable)
        return newVariable
    }

    fun readMode() {
        writeMode = false
    }

    fun writeMode() {
        writeMode = true
    }

    fun reset() {
        list.fori {
            it?.reset()
        }
    }

    operator fun get(index: Int): Resettable? {
        return list[index]
    }

    operator fun set(index: Int, value: Resettable?) {
        list[index] = value
    }


    inner class DrawStateDouble(private val resetValue: Double, val index: Int) : Resettable {

        var wasChanged = true
            private set

        var value: Double = resetValue
            set(value) {
                field = value
                wasChanged = true
                list[index] = this
            }

        override fun reset() {
            value = resetValue
            wasChanged = false
            list[index] = null
        }

    }
}

interface Resettable {
    fun reset()
}

abstract class DrawStateVariable<T>(
        protected val list: DrawStateVariableList,
        val variable: T,
        val index: Int) : Resettable {

    var wasChanged = true
        private set

    fun markReset() {
        wasChanged = false
        list[index] = null
    }

    fun markChanged() {
        wasChanged = true
        list[index] = this
    }

}

class TransformationState(private val origin: OriginState) : Resettable {
    private val list = DrawStateVariableList()
    private val matrix = list.add(Matrix4f().identity()) { it.identity() }
    var userMatrix: Matrix4f? = null

    val translation = list.addVector3(0f)
    val scale = list.addVector3(1f)

    private val zAxis = Vector3f(0f, 0f, 1f)
    var rotation = list.addDouble(0.0)

    private val skewMatrix = Matrix4f().identity()
    val skew = list.addVector2(0.0)

    fun getTransformationMatrix(): Matrix4f {
        val matrix = matrix.variable
        matrix.translate(translation.variable)

        if (rotation.wasChanged)
            matrix.rotate(rotation.value.toFloat(), zAxis)

        addSkewToMatrix(matrix)
        addOriginToMatrix(matrix)

        matrix.scale(scale.variable)

        userMatrix?.let { matrix.mul(it) }
        userMatrix = null
        return matrix
    }

    private fun addSkewToMatrix(matrix: Matrix4f) {
        if (skew.wasChanged) {
            skewMatrix.m10(tan(-skew.x.toFloat()))
            skewMatrix.m01(tan(skew.y.toFloat()))

            matrix.mul(skewMatrix)
        }
    }

    private fun addOriginToMatrix(matrix: Matrix4f) {
        if (origin.wasChanged) {
            val pivotX = origin.normalized.x.toFloat()*scale.x + origin.relative.x.toFloat()
            val pivotY = origin.normalized.y.toFloat()*scale.y + origin.relative.y.toFloat()
            matrix.translate(-pivotX, -pivotY, 0f)
        }
    }

    fun setDepth(z: Float) {
        translation.set(z = translation.z-z)
    }

    override fun reset() {
        list.reset()
    }
}

class OriginState : Resettable {
    private val list = DrawStateVariableList()

    val normalized = list.addVector2()
    val relative = list.addVector2()

    val wasChanged: Boolean
        get() = normalized.wasChanged || relative.wasChanged

    override fun reset() {
        list.reset()
    }
}

class Vector3Variable(list: DrawStateVariableList, index: Int, private val resetValue: Float = 0f)
    : DrawStateVariable<Vector3f>(list, Vector3f(), index) {

    var x: Float
        get() = variable.x
        set(value) = set(value, y, z)
    var y: Float
        get() = variable.y
        set(value) = set(x, value, z)
    var z: Float
        get() = variable.z
        set(value) = set(x, y, value)


    override fun reset() {
        markReset()
        variable.set(resetValue)
    }

    fun set(x: Number = this.x, y: Number = this.y, z: Number = this.z) {
        markChanged()
        variable.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun set(xy: Vector) {
        markChanged()
        variable.set(xy.x.toFloat(), xy.y.toFloat(), z)
    }

    fun set(xy: InlineVector) {
        markChanged()
        variable.set(xy.x.toFloat(), xy.y.toFloat(), z)
    }
}

class Vector2Variable(list: DrawStateVariableList, vector: DynamicVector, index: Int, private val resetValue: Double = 0.0)
    : DrawStateVariable<DynamicVector>(list, vector, index) {

    var x: Double
        get() = variable.x
        set(value) = set(value, y)
    var y: Double
        get() = variable.y
        set(value) = set(x, value)

    constructor(list: DrawStateVariableList, index: Int, resetValue: Double = 0.0)
        : this(list, DynamicVector.create(), index, resetValue)

    override fun reset() {
        markReset()
        variable.set(resetValue)
    }

    fun set(x: Number = this.x, y: Number = this.y) {
        markChanged()
        variable.set(x.toDouble(), y.toDouble())
    }

    fun set(xy: Vector) {
        markChanged()
        variable.set(xy)
    }

    fun set(xy: InlineVector) {
        markChanged()
        variable.set(xy)
    }
}
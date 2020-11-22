package com.mechanica.engine.drawer.state

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.util.extensions.fori
import org.joml.Vector3f


class DrawStateVariableList {
    val elements = ArrayList<Resettable?>()
    var writeMode = true
        private set

    fun addVector3(resetValue: Float = 0f): Vector3Variable {
        val newVariable = Vector3Variable(this, elements.size, resetValue)
        elements.add(newVariable)
        return newVariable
    }

    fun addVector2(resetValue: Double = 0.0): Vector2Variable {
        return addVector2(DynamicVector.create(), resetValue)
    }

    fun addVector2(vector: DynamicVector, resetValue: Double = 0.0): Vector2Variable {
        val newVariable = Vector2Variable(this, vector, elements.size, resetValue)
        elements.add(newVariable)
        return newVariable
    }

    fun addColor(color: DynamicColor = DynamicColor.create(), resetColor: Color? = null): ColorVariable {
        val newVariable = ColorVariable(this, color, elements.size, resetColor)
        elements.add(newVariable)
        return newVariable
    }

    fun <T> addVariable(variable: T, resetter: GenericVariable<T>.(T) -> Unit): GenericVariable<T> {
        val newVariable = GenericVariable(this, variable, elements.size, resetter)
        elements.add(newVariable)
        return newVariable
    }

    fun addDouble(value: Double): DrawStateDouble {
        val newVariable = DrawStateDouble(value, elements.size)
        elements.add(newVariable)
        return newVariable
    }

    fun <T> add(variable: T, resetter: (T)->Unit): DrawStateVariable<T> {
        val newVariable = object : DrawStateVariable<T>(this, variable, elements.size) {
            override fun reset() {
                resetter(this.variable)
            }
        }
        elements.add(newVariable)
        return newVariable
    }

    fun <T : Resettable> add(resetter: T): T {
        elements.add(resetter)
        return resetter
    }

    fun readMode() {
        writeMode = false
    }

    fun writeMode() {
        writeMode = true
    }

    fun reset() {
        elements.fori {
            it?.reset()
        }
    }

    operator fun get(index: Int): Resettable? {
        return elements[index]
    }

    operator fun set(index: Int, value: Resettable?) {
        elements[index] = value
    }

    inline fun forEach(action: (Resettable?) -> Unit) {
        elements.fori(action)
    }


    inner class DrawStateDouble(private val resetValue: Double, val index: Int) : Resettable {

        var wasChanged = true
            private set

        var value: Double = resetValue
            set(value) {
                field = value
                wasChanged = true
                elements[index] = this
            }

        override fun reset() {
            value = resetValue
            wasChanged = false
            elements[index] = null
        }

    }
}

interface Resettable {
    fun reset()
}

abstract class DrawStateVariable<T>(
        protected val list: DrawStateVariableList,
        open val variable: T,
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
    : DrawStateVariable<DynamicVector>(list, vector, index), DynamicVector {

    override var x: Double
        get() = variable.x
        set(value) = set(value, y)
    override var y: Double
        get() = variable.y
        set(value) = set(x, value)

    override fun reset() {
        markReset()
        variable.set(resetValue)
    }

    override fun set(x: Double, y: Double) {
        markChanged()
        variable.set(x, y)
    }

}

class ColorVariable(list: DrawStateVariableList, color: DynamicColor, index: Int, private val resetColor: Color? = null)
    : DrawStateVariable<DynamicColor>(list, color, index), DynamicColor {

    override var r: Double
        get() = variable.r
        set(value) = set(red = value)
    override var g: Double
        get() = variable.g
        set(value) = set(green = value)
    override var b: Double
        get() = variable.b
        set(value) = set(blue = value)
    override var a: Double
        get() = variable.a
        set(value) = set(alpha = value)

    override fun reset() {
        markReset()
        a = 1.0
        resetColor?.let { set(it) }
    }

    override fun set(red: Double, green: Double, blue: Double, alpha: Double) {
        markChanged()
        variable.set(red, green, blue, alpha)
    }

}

class GenericVariable<T>(list: DrawStateVariableList, variable: T, index: Int, private val resetter: GenericVariable<T>.(T) -> Unit)
    : DrawStateVariable<T>(list, variable, index) {

    override var variable: T = variable
        set(value) {
            field = value
            markChanged()
        }

    override fun reset() {
        resetter(this, variable)
        markReset()
    }

}
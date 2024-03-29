package com.mechanica.engine.drawer.state

import com.cave.library.angle.Angle
import com.cave.library.angle.radians
import com.cave.library.color.Color
import com.cave.library.color.VariableColor
import com.cave.library.vector.vec2.*
import com.cave.library.vector.vec3.MutableVector3
import com.mechanica.engine.util.extensions.fori


class DrawStateVariableList {
    val elements = ArrayList<Resettable?>()
    var writeMode = true
        private set

    fun addVector3(resetValue: Double = 0.0): Vector3Variable {
        val newVariable = Vector3Variable(this, elements.size, resetValue)
        elements.add(newVariable)
        return newVariable
    }

    fun addVector2(resetValue: Double = 0.0): Vector2Variable {
        return addVector2(MutableVector2.create(), resetValue)
    }

    fun addVector2(vector: MutableVector2, resetValue: Double = 0.0): Vector2Variable {
        val newVariable = Vector2Variable(this, vector, elements.size, resetValue)
        elements.add(newVariable)
        return newVariable
    }

    fun addAngleVector2(vector: VariableAngleVector2, resetValue: Angle = 0.0.radians): AngleVector2Variable {
        val newVariable = AngleVector2Variable(this, vector, elements.size, resetValue)
        elements.add(newVariable)
        return newVariable
    }

    fun addAngleVector2(resetValue: Angle = 0.0.radians): AngleVector2Variable {
        return addAngleVector2(VariableAngleVector2.create(), resetValue)
    }

    fun addColor(color: VariableColor = VariableColor.create(), resetColor: Color? = null): ColorVariable {
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

    fun addRadian(value: Angle): DrawStateRadian {
        val newVariable = DrawStateRadian(value, elements.size)
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

    inner class DrawStateRadian(private val resetValue: Angle, val index: Int) : Resettable {

        var wasChanged = true
            private set

        var value: Angle = resetValue
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


class Vector3Variable(list: DrawStateVariableList, index: Int, private val resetValue: Double = 0.0)
    : DrawStateVariable<MutableVector3>(list, MutableVector3.create(), index) {

    var x: Double
        get() = variable.x
        set(value) = set(value, y, z)
    var y: Double
        get() = variable.y
        set(value) = set(x, value, z)
    var z: Double
        get() = variable.z
        set(value) = set(x, y, value)


    override fun reset() {
        markReset()
        variable.set(resetValue, resetValue, resetValue)
    }

    fun set(x: Number = this.x, y: Number = this.y, z: Number = this.z) {
        markChanged()
        variable.set(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun set(xy: Vector2) {
        markChanged()
        variable.set(xy.x, xy.y, z)
    }

    fun set(xy: InlineVector) {
        markChanged()
        variable.set(xy.x, xy.y, z)
    }
}

class Vector2Variable(list: DrawStateVariableList, vector: MutableVector2, index: Int, private val resetValue: Double = 0.0)
    : DrawStateVariable<MutableVector2>(list, vector, index), MutableVector2 {

    override var x: Double
        get() = variable.x
        set(value) = set(value, y)
    override var y: Double
        get() = variable.y
        set(value) = set(x, value)

    override fun reset() {
        markReset()
        variable.set(resetValue, resetValue)
    }

    override fun set(x: Double, y: Double) {
        markChanged()
        variable.set(x, y)
    }

    override fun toString() = Vector2.toString(this)
}

class AngleVector2Variable(list: DrawStateVariableList, vector: VariableAngleVector2, index: Int, private val resetValue: Angle = 0.0.radians)
    : DrawStateVariable<VariableAngleVector2>(list, vector, index), VariableAngleVector2 {

    override var x: Angle
        get() = variable.x
        set(value) = set(value, y)
    override var y: Angle
        get() = variable.y
        set(value) = set(x, value)

    override fun reset() {
        markReset()
        variable.set(resetValue, resetValue)
    }

    override fun set(x: Angle, y: Angle) {
        markChanged()
        variable.set(x, y)
    }

    override fun toString() = AngleVector2.toString(this)
}

class ColorVariable(list: DrawStateVariableList, color: VariableColor, index: Int, private val resetColor: Color? = null)
    : DrawStateVariable<VariableColor>(list, color, index), VariableColor {

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
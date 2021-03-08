package com.mechanica.engine.shaders.attributes

import com.cave.library.color.Color
import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec3.Vector3
import com.cave.library.vector.vec4.Vector4
import com.mechanica.engine.shaders.buffers.FloatBufferObject
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType

interface AttributeArray : Bindable {
    val vertexCount: Int

    companion object {
        fun create(floats: FloatArray, location: Int, type: ShaderType<*> = ShaderType.float()) : AttributeArrayForFloats<*> {
            val attribute = FloatAttributeArray(floats)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(floats: FloatArray, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = FloatAttributeArray(floats)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<out Vector2>, location: Int, type: ShaderType<*> = ShaderType.vec2()) : AttributeArrayForFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<out Vector2>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun createPositionArray(array: FloatArray): FloatAttributeArray {
            val attribute = FloatAttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createPositionArray(array: Array<out Vector2>): Vec2AttributeArray {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createPositionArray(array: Array<Vector3>): Vec3AttributeArray {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createTextureArray(array: FloatArray): FloatAttributeArray {
            val attribute = FloatAttributeArray(array)
            attribute.attachTo(1, ShaderType.vec2())
            return attribute
        }

        fun createTextureArray(array: Array<out Vector2>): Vec2AttributeArray {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(1, ShaderType.vec2())
            return attribute
        }

        fun create(array: Array<Vector3>, location: Int, type: ShaderType<*> = ShaderType.vec3()) : AttributeArrayForFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector3>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<Vector4>, location: Int, type: ShaderType<*> = ShaderType.vec4()) : AttributeArrayForFloats<*> {
            val attribute = Vec4AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector4>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = Vec4AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<out Color>, location: Int, type: ShaderType<*> = ShaderType.vec4()) : AttributeArrayForFloats<*> {
            val attribute = ColorAttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<out Color>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = ColorAttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }
    }
}

abstract class AttributeArrayForFloats<T> : AttributeArray {
    private var bindable: Bindable? = null
    private var type: ShaderType<*>? = null

    abstract val value: T

    protected var bufferLoader: FloatBufferObject? = null

    fun attachTo(variable: AttributeVar<*>) {
        bindable = variable
        type = variable.type
        storeDataToBufferLoader(variable.type)
    }

    fun attachTo(location: Int, type: ShaderType<*>) {
        val locatable = GlslLocation.create(location)
        this.type = type
        bindable = FloatAttributeBinder.create(locatable, type)
        storeDataToBufferLoader(type)
    }

    private fun storeDataToBufferLoader(type: ShaderType<*>) {
        val floats = valueToFloatArray(type.coordinateSize)
        val buffer = bufferLoader
        if (buffer == null) {
            bufferLoader = FloatBufferObject.createArrayBuffer(floats)
        } else {
            buffer.floats = floats
            buffer.storeData(floats.size.toLong())
        }
    }

    override fun bind() {
        bufferLoader?.bind()
        bindable?.bind()
    }

    override fun unbind() {

    }

    fun updateBuffer() {
        val type = this.type
        if (type != null) {
            storeDataToBufferLoader(type)
        }
    }

    abstract fun valueToFloatArray(coordinateSize: Int): FloatArray

    companion object {
        fun <T> Array<T>.toFloatArray(coordinateSize: Int, converter: (T, Int) -> Float): FloatArray {

            val floatArray = FloatArray(coordinateSize*this.size)

            return fillFloatArray(floatArray, coordinateSize, converter)
        }

        fun <T> Array<T>.fillFloatArray(floats: FloatArray, coordinateSize: Int, converter: (T, Int) -> Float): FloatArray {

            for (i in this.indices) {
                fun FloatArray.setAt(coordinate: Int, value: Float) {
                    if (coordinate < coordinateSize) this[i*coordinateSize + coordinate] = value
                }
                var coordinate = 0
                while (coordinate < coordinateSize) {
                    floats.setAt(coordinate, converter(this[i], coordinate++))
                }
            }
            return floats
        }
    }
}

class FloatAttributeArray(override var value: FloatArray) : AttributeArrayForFloats<FloatArray>() {
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int) = value

}

abstract class AttributeArrayMultiCoordForFloats<T> : AttributeArrayForFloats<Array<out T>>() {
    override val vertexCount: Int
        get() = value.size

    protected abstract val valueToFloatConverter: (T, Int) -> Float

    override fun valueToFloatArray(coordinateSize: Int): FloatArray {
        val buffer = bufferLoader
        if (buffer != null && buffer.floats.size >= coordinateSize*vertexCount) {
            value.fillFloatArray(buffer.floats, coordinateSize, valueToFloatConverter)
            return buffer.floats
        }
        return value.toFloatArray(coordinateSize, valueToFloatConverter)
    }
}

class Vec2AttributeArray(array: Array<out Vector2>) : AttributeArrayMultiCoordForFloats<VariableVector2>() {
    override var value: Array<VariableVector2> = Array(array.size) { VariableVector2.create(array[it]) }

    override val valueToFloatConverter: (VariableVector2, Int) -> Float = { v, coord -> v.getOrZero(coord).toFloat() }
}

class Vec3AttributeArray(override var value: Array<Vector3>) : AttributeArrayMultiCoordForFloats<Vector3>() {
    override val valueToFloatConverter: (Vector3, Int) -> Float = { v, coord -> if (coord < 3) v[coord].toFloat() else 0f }

}

class Vec4AttributeArray(override var value: Array<Vector4>) : AttributeArrayMultiCoordForFloats<Vector4>() {
    override val valueToFloatConverter: (Vector4, Int) -> Float = { v, coord -> if (coord < 4) v[coord].toFloat() else 0f }
}

class ColorAttributeArray(override var value: Array<out Color>) : AttributeArrayMultiCoordForFloats<Color>() {
    override val valueToFloatConverter: (Color, Int) -> Float = { v, coord -> v.getOrZero(coord).toFloat() }
}

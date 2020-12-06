package com.mechanica.engine.shader.attributes

import com.mechanica.engine.color.Color
import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.GLPrimitive
import org.joml.Vector3f
import org.joml.Vector4f

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

        fun create(array: Array<out Vector>, location: Int, type: ShaderType<*> = ShaderType.vec2()) : AttributeArrayForFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<out Vector>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun createPositionArray(array: FloatArray): FloatAttributeArray {
            val attribute = FloatAttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createPositionArray(array: Array<out Vector>): Vec2AttributeArray{
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createPositionArray(array: Array<Vector3f>): Vec3AttributeArray {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(0, ShaderType.vec3())
            return attribute
        }

        fun createTextureArray(array: FloatArray): FloatAttributeArray {
            val attribute = FloatAttributeArray(array)
            attribute.attachTo(1, ShaderType.vec2())
            return attribute
        }

        fun createTextureArray(array: Array<out Vector>): Vec2AttributeArray {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(1, ShaderType.vec2())
            return attribute
        }

        fun create(array: Array<Vector3f>, location: Int, type: ShaderType<*> = ShaderType.vec3()) : AttributeArrayForFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector3f>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<Vector4f>, location: Int, type: ShaderType<*> = ShaderType.vec4()) : AttributeArrayForFloats<*> {
            val attribute = Vec4AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector4f>, attributeVar: AttributeVar<*>) : AttributeArrayForFloats<*> {
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

interface VertexBufferLoader : Bindable {
    val id: Int
    val type: GLPrimitive

    fun storeData(arraySize: Long) {
        bind()
        val capacityNeeded = arraySize*type.byteSize
        if (getExistingBufferSize() < capacityNeeded) {
            initiateBuffer(capacityNeeded)
        }
        storeSubData(0)
    }

    fun initiateBuffer(byteCapacity: Long)
    fun storeSubData(offset: Long)
    fun getExistingBufferSize(): Long
}

abstract class FloatBufferLoader : VertexBufferLoader {
    abstract var floats: FloatArray
    override val type = MechanicaLoader.glPrimitives.glFloat

    companion object {
        fun create(floats: FloatArray): FloatBufferLoader {
            return MechanicaLoader.shaderLoader.createFloatBufferLoader(floats)
        }
    }
}

abstract class AttributeArrayForFloats<T> : AttributeArray {
    private var bindable: Bindable? = null
    private var type: ShaderType<*>? = null

    abstract val value: T

    protected var bufferLoader: FloatBufferLoader? = null

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
            bufferLoader = FloatBufferLoader.create(floats)
        } else {
            buffer.floats = floats
        }

        bufferLoader?.storeData(floats.size.toLong())
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

class Vec2AttributeArray(array: Array<out Vector>) : AttributeArrayMultiCoordForFloats<DynamicVector>() {
    override var value: Array<DynamicVector> = Array(array.size) { DynamicVector.create(array[it]) }

    override val valueToFloatConverter: (DynamicVector, Int) -> Float = { v, coord -> v.getOrZero(coord).toFloat() }
}

class Vec3AttributeArray(override var value: Array<Vector3f>) : AttributeArrayMultiCoordForFloats<Vector3f>() {
    override val valueToFloatConverter: (Vector3f, Int) -> Float = { v, coord -> if (coord < 3) v[coord] else 0f }

}

class Vec4AttributeArray(override var value: Array<Vector4f>) : AttributeArrayMultiCoordForFloats<Vector4f>() {
    override val valueToFloatConverter: (Vector4f, Int) -> Float = { v, coord -> if (coord < 4) v[coord] else 0f }
}

class ColorAttributeArray(override var value: Array<out Color>) : AttributeArrayMultiCoordForFloats<Color>() {
    override val valueToFloatConverter: (Color, Int) -> Float = { v, coord -> v.getOrZero(coord).toFloat() }
}

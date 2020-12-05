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
        fun create(floats: FloatArray, location: Int, type: ShaderType<*> = ShaderType.float()) : AttributeArrayFloats<*> {
            val attribute = FloatAttributeArray(floats)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(floats: FloatArray, attributeVar: AttributeVar<*>) : AttributeArrayFloats<*> {
            val attribute = FloatAttributeArray(floats)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<out Vector>, location: Int, type: ShaderType<*> = ShaderType.vec2()) : AttributeArrayFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<out Vector>, attributeVar: AttributeVar<*>) : AttributeArrayFloats<*> {
            val attribute = Vec2AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun createPositionArray(array: FloatArray) = create(array, Attribute.positionLocation, ShaderType.vec3())
        fun createPositionArray(array: Array<out Vector>) = create(array, Attribute.positionLocation, ShaderType.vec3())
        fun createPositionArray(array: Array<Vector3f>) = create(array, Attribute.positionLocation, ShaderType.vec3())
        fun createTextureArray(array: Array<out Vector>) = create(array, Attribute.texCoordsLocation, ShaderType.vec2())

        fun create(array: Array<Vector3f>, location: Int, type: ShaderType<*> = ShaderType.vec3()) : AttributeArrayFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector3f>, attributeVar: AttributeVar<*>) : AttributeArrayFloats<*> {
            val attribute = Vec3AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<Vector4f>, location: Int, type: ShaderType<*> = ShaderType.vec4()) : AttributeArrayFloats<*> {
            val attribute = Vec4AttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<Vector4f>, attributeVar: AttributeVar<*>) : AttributeArrayFloats<*> {
            val attribute = Vec4AttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }

        fun create(array: Array<out Color>, location: Int, type: ShaderType<*> = ShaderType.vec4()) : AttributeArrayFloats<*> {
            val attribute = ColorAttributeArray(array)
            attribute.attachTo(location, type)
            return attribute
        }

        fun create(array: Array<out Color>, attributeVar: AttributeVar<*>) : AttributeArrayFloats<*> {
            val attribute = ColorAttributeArray(array)
            attribute.attachTo(attributeVar)
            return attribute
        }
    }
}

interface VertexBufferLoader : Bindable {
    val id: Int
    val type: GLPrimitive

    fun storeData() {
        bind()
        initiateBuffer()
        storeSubData(0)
    }

    fun initiateBuffer()
    fun storeSubData(offset: Long)
}

abstract class FloatBufferLoader : VertexBufferLoader {
    abstract val floats: FloatArray
    override val type = MechanicaLoader.glPrimitives.glFloat
    val byteCapacity: Long
        get() = floats.size.toLong()*type.byteSize
}

abstract class AttributeArrayFloats<T> : AttributeArray {
    private var bindable: Bindable? = null
    abstract val value: T

    private var bufferLoader: FloatBufferLoader? = null

    fun attachTo(variable: AttributeVar<*>) {
        bindable = variable
        storeDataToBufferLoader(variable.type)
    }

    fun attachTo(location: Int, type: ShaderType<*>) {
        val locatable = GlslLocation.create(location)
        bindable = loader.createFloatAttributeBinder(locatable, type)
        storeDataToBufferLoader(type)
    }

    override fun bind() {
        bufferLoader?.bind()
        bindable?.bind()
    }

    override fun unbind() {

    }

    private fun storeDataToBufferLoader(type: ShaderType<*>) {
        val floats = valueToFloatArray(type.coordinateSize)
        bufferLoader = loader.createFloatBufferLoader(floats)
        bufferLoader?.storeData()
    }

    abstract fun valueToFloatArray(coordinateSize: Int): FloatArray

    companion object {
        private val loader = MechanicaLoader.shaderLoader

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

class FloatAttributeArray(override val value: FloatArray) : AttributeArrayFloats<FloatArray>() {
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int) = value

}

class Vec2AttributeArray(array: Array<out Vector>) : AttributeArrayFloats<Array<DynamicVector>>() {
    override val value: Array<DynamicVector> = Array(array.size) { DynamicVector.create(array[it]) }
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int): FloatArray {
        return value.toFloatArray(coordinateSize) { v, i -> v.getOrZero(i).toFloat() }
    }
}

class Vec3AttributeArray(override val value: Array<Vector3f>) : AttributeArrayFloats<Array<Vector3f>>() {
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int): FloatArray {
        return value.toFloatArray(coordinateSize) { v, i -> if (i < 3) v[i] else 0f }
    }
}

class Vec4AttributeArray(override val value: Array<Vector4f>) : AttributeArrayFloats<Array<Vector4f>>() {
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int): FloatArray {
        return value.toFloatArray(coordinateSize) { v, i -> if (i < 4) v[i] else 0f }
    }
}

class ColorAttributeArray(override val value: Array<out Color>) : AttributeArrayFloats<Array<out Color>>() {
    override val vertexCount: Int
        get() = value.size

    override fun valueToFloatArray(coordinateSize: Int): FloatArray {
        return value.toFloatArray(coordinateSize) { v, i -> v.getOrZero(i).toFloat() }
    }
}
//
//abstract class Vec3AttributeArray private constructor(array: FloatArray) : AttributeArrayFloats<Vector3f>(array, 3) {
//
//    constructor(array: Array<Vector>)
//            : this(array.toFloatArray(3) { v, i -> v.getOrZero(i).toFloat() })
//
//    constructor(array: Array<Vector3f>)
//            : this(array.toFloatArray(3) { v, i -> if (i < 3) v[i] else 0f })
//
//    constructor(array: Array<Vector4f>)
//            : this(array.toFloatArray(3) { v, i -> if (i < 4) v[i] else 0f })
//
//}
//
//abstract class Vec4AttributeArray private constructor(array: FloatArray) : AttributeArrayFloats<Vector4f>(array, 4) {
//
//    constructor(array: Array<Vector>)
//            : this(array.toFloatArray(4) { v, i -> v.getOrZero(i).toFloat() })
//
//    constructor(array: Array<Vector3f>)
//            : this(array.toFloatArray(4) { v, i -> if (i < 3) v[i] else 0f })
//
//    constructor(array: Array<Vector4f>)
//            : this(array.toFloatArray(4) { v, i -> if (i < 4) v[i] else 0f })
//
//}

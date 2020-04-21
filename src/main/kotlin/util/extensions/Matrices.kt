package util.extensions

import memory.MemoryStack
import org.joml.Matrix4f
import org.joml.Vector3f
import util.units.LightweightVector

private val stack = MemoryStack { Vector3f() }

val Matrix4f.scaleX: Float
    get() = getProperty {
        getScale(it)
        it.x
    }

val Matrix4f.scaleY: Float
    get() = getProperty {
        getScale(it)
        it.y
    }

val Matrix4f.scaleXY: LightweightVector
    get() = getProperty2f {
        getScale(it)
        vec(it.x, it.y)
    }

val Matrix4f.scaleZ: Float
    get() = getProperty {
        getScale(it)
        it.z
    }

val Matrix4f.translateX: Float
    get() = getProperty {
        getTranslation(it)
        it.x
    }

val Matrix4f.translateY: Float
    get() = getProperty {
        getTranslation(it)
        it.y
    }

val Matrix4f.translateXY: LightweightVector
    get() = getProperty2f {
        getTranslation(it)
        vec(it.x, it.y)
    }

val Matrix4f.translateZ: Float
    get() = getProperty {
        getTranslation(it)
        it.z
    }

private inline fun Matrix4f.getProperty(getter: (Vector3f) -> Float): Float {
    val vec = stack.allocate()
    val value = getter(vec)
    stack.freeMemory(vec)
    return value
}

private inline fun Matrix4f.getProperty2f(getter: (Vector3f) -> LightweightVector): LightweightVector {
    val vec = stack.allocate()
    val value = getter(vec)
    stack.freeMemory(vec)
    return value
}

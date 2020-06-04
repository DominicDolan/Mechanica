package com.mechanica.engine.util.extensions

import com.mechanica.engine.memory.SimulatedStack
import org.joml.Matrix4f
import org.joml.Vector3f
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.vec

private val stack = SimulatedStack { Vector3f() }

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

private inline fun getProperty(getter: (Vector3f) -> Float): Float {
    val vec = stack.allocate()
    val value = getter(vec)
    stack.freeMemory(vec)
    return value
}

private inline fun getProperty2f(getter: (Vector3f) -> LightweightVector): LightweightVector {
    val vec = stack.allocate()
    val value = getter(vec)
    stack.freeMemory(vec)
    return value
}
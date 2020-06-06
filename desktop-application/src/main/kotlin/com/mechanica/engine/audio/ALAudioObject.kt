package com.mechanica.engine.audio

import com.mechanica.engine.memory.useMemoryStack
import org.joml.Vector3f
import org.lwjgl.openal.AL10

abstract class ALAudioObject : AudioObject {
    private val _position: Vector3f by lazy { createVector3fSetterInterept(AL10.AL_POSITION, this::set3f) }
    override var position: Vector3f
        get() {
            getVector3f(AL10.AL_POSITION, _position)
            return _position
        }
        set(value) {
            _position.set(value)
        }

    private val _velocity: Vector3f by lazy { createVector3fSetterInterept(AL10.AL_VELOCITY, this::set3f) }
    override var velocity: Vector3f
        get() {
            getVector3f(AL10.AL_VELOCITY, _velocity)
            return _velocity
        }
        set(value) {
            _velocity.set(value)
        }

    protected abstract fun getVector3f(property: Int, vec: Vector3f)

    protected abstract fun set3f(property: Int, x: Float, y: Float, z: Float)


    protected fun createVector3fSetterInterept(property: Int, setter: (Int, Float, Float, Float) -> Unit) = object : Vector3f() {
        override fun set(x: Float, y: Float, z: Float): Vector3f {
            setter(property, x, y, z)
            return super.set(x, y, z)
        }
    }
}
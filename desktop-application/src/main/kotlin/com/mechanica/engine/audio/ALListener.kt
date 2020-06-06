package com.mechanica.engine.audio

import com.mechanica.engine.memory.useMemoryStack
import org.joml.Vector3f
import org.lwjgl.openal.AL10.*
import java.nio.FloatBuffer

class ALListener : ALAudioObject(), Listener {
    override var gain: Float
        get() = alGetListenerf(AL_GAIN)
        set(value) { alListenerf(AL_GAIN, value )}

    override val orientation = ALOrientation()

    override var distanceModel: Listener.DistanceModel = Listener.DistanceModel.INVERSE.also { alDistanceModel(distanceModelEnumToALNum(it)) }
        set(value) { alDistanceModel(distanceModelEnumToALNum(value)) }

    override fun getVector3f(property: Int, vec: Vector3f) {
        useMemoryStack {
            val floats = floats(0f, 0f, 0f)
            alGetListenerfv(property, floats)
            vec.set(floats[0], floats[1], floats[2])
        }
    }

    override fun set3f(property: Int, x: Float, y: Float, z: Float) {
        alListener3f(property, x, y, z)
    }

    inner class ALOrientation : Listener.Orientaion {
        private val _at: Vector3f = createVector3fSetterInterept(AL_ORIENTATION, this::setAt)
        override var at: Vector3f
            get() {
                get()
                return _at
            }
            set(value) {
                _at.set(value)
            }

        private val _up: Vector3f = createVector3fSetterInterept(AL_ORIENTATION, this::setUp)
        override var up: Vector3f
            get() {
                get()
                return _up
            }
            set(value) {
                _up.set(value)
            }

        private fun get() {
            useMemoryStack {
                val floats = mallocFloat(6)
                alGetListenerfv(AL_ORIENTATION, floats)
                getValues(floats)
            }
        }

        private fun setUp(property: Int = AL_ORIENTATION, x: Float, y: Float, z: Float) = set(property, _at.x, _at.y, _at.z, x, y, z)
        private fun setAt(property: Int = AL_ORIENTATION, x: Float, y: Float, z: Float) = set(property, x, y, z, _up.x, _up.y, _up.z)

        private fun set(property: Int, atX: Float, atY: Float, atZ: Float, upX: Float, upY: Float, upZ: Float) {
            useMemoryStack {
                val floats = mallocFloat(6)
                floats.put(atX).put(atY).put(atZ)
                        .put(upX).put(upY).put(upZ)
                alListenerfv(property, floats)
            }
        }

        private fun getValues(buffer: FloatBuffer) {
            buffer.getVector3f(at)
            buffer.getVector3f(up)
            buffer.position(0)
        }

        private fun FloatBuffer.getVector3f(vec: Vector3f) {
            vec.set(get(), get(), get())
        }
    }

}
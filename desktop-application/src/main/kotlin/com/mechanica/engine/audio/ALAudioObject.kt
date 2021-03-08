package com.mechanica.engine.audio

import com.cave.library.vector.vec3.VariableVector3
import com.cave.library.vector.vec3.Vector3
import org.lwjgl.openal.AL10

abstract class ALAudioObject : AudioObject {
    private val _position: ALVector3 by lazy { ALVector3(AL10.AL_POSITION, FloatArray(3)) }
    override var position: VariableVector3
        get() {
            getVector3(_position)
            return _position
        }
        set(value) {
            _position.set(value)
        }

    private val _velocity: ALVector3 by lazy { ALVector3(AL10.AL_VELOCITY, FloatArray(3)) }
    override var velocity: VariableVector3
        get() {
            getVector3(_velocity)
            return _velocity
        }
        set(value) {
            _velocity.set(value)
        }

    protected abstract fun getVector3(vec: ALVector3)

    protected abstract fun setVector3(vec: ALVector3)


    inner class ALVector3(val property: Int, val array: FloatArray, private val offset: Int = 0) : VariableVector3 {
        private var _x: Double
            get() = array[offset].toDouble()
            set(value) {
                array[offset] = value.toFloat()
            }
        override var x: Double
            get() = _x
            set(value) {
                _x = value
                setVector3(this)
            }

        private var _y: Double
            get() = array[offset+1].toDouble()
            set(value) {
                array[offset+1] = value.toFloat()
            }
        override var y: Double
            get() = _y
            set(value) {
                _y = value
                setVector3(this)
            }

        private var _z: Double
            get() = array[offset+2].toDouble()
            set(value) {
                array[offset+2] = value.toFloat()
            }
        override var z: Double
            get() = _z
            set(value) {
                _z = value
                setVector3(this)
            }

        override fun set(x: Double, y: Double, z: Double) {
            setVector3(this)
            _x = x
            _y = y
            _z = z
        }

        override fun toString() = Vector3.toString(this)

    }
}
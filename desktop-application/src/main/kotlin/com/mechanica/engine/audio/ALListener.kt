package com.mechanica.engine.audio

import com.cave.library.vector.vec3.Vector3
import org.lwjgl.openal.AL10.*

class ALListener : ALAudioObject(), Listener {
    override var gain: Float
        get() = alGetListenerf(AL_GAIN)
        set(value) { alListenerf(AL_GAIN, value )}

    override val orientation = ALOrientation()

    override var distanceModel: Listener.DistanceModel = Listener.DistanceModel.INVERSE.also { alDistanceModel(distanceModelEnumToALNum(it)) }
        set(value) { alDistanceModel(distanceModelEnumToALNum(value)) }

    override fun getVector3(vec: ALVector3) {
        alGetListenerfv(vec.property, vec.array)
    }

    override fun setVector3(vec: ALVector3) {
        alListenerfv(vec.property, vec.array)
    }

    inner class ALOrientation : Listener.Orientaion {
        private val floats = FloatArray(6)
        private val _at: ALVector3 = ALVector3(AL_ORIENTATION, floats, offset = 0)
        override var at: Vector3
            get() {
                getVector3(_at)
                return _at
            }
            set(value) {
                _at.set(value)
            }

        private val _up: ALVector3 = ALVector3(AL_ORIENTATION, floats, offset = 3)
        override var up: Vector3
            get() {
                getVector3(_up)
                return _up
            }
            set(value) {
                _up.set(value)
            }
    }

}
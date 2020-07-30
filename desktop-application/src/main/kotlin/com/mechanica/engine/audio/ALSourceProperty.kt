package com.mechanica.engine.audio

import org.lwjgl.openal.AL10
import kotlin.reflect.KProperty

class ALSourceProperty(private val sourceId: Int) {

    fun createProperty(property: Int) = DelegatedProperty(property)

    private fun getFloat(property: Int) = AL10.alGetSourcef(sourceId, property)
    private fun setFloat(property: Int, float: Float) = AL10.alSourcef(sourceId, property, float)

    inner class DelegatedProperty(private val property: Int) {
        operator fun getValue(thisRef: SoundSource, property: KProperty<*>): Float {
            return getFloat(this.property)
        }

        operator fun setValue(thisRef: SoundSource, property: KProperty<*>, value: Float) {
            setFloat(this.property, value)
        }
    }
}
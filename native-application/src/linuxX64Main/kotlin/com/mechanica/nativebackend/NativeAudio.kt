@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import cnames.structs.ALCdevice
import cnames.structs.ALCcontext
import com.cave.library.vector.vec3.MutableVector3
import com.cave.library.vector.vec3.Vector3
import com.mechanica.engine.audio.*
import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.AudioContext
import com.mechanica.engine.context.loader.AudioFactory
import com.mechanica.engine.resources.Resource
import com.mechanica.nativebackend.openal.*
import kotlinx.cinterop.*
import kotlin.math.roundToInt

class NativeAudioFactory : AudioFactory {
    private val sharedListener = NativeListener()
    override fun sound(res: Resource): Sound = NativeSound(res)
    override fun source(sound: Sound): SoundSource = NativeSoundSource(sound)
    override fun listener(): Listener = sharedListener
}

class NativeAudioContext(override val listener: Listener) : AudioContext {
    private var handle: CPointer<ALCdevice>? = null
    private var context: CPointer<ALCcontext>? = null
    override val device: AudioDevice get() = object : AudioDevice {
        override val id: Long = checkNotNull(handle).rawValue.toLong()
    }
    override var majorVersion = 0
        private set
    override var minorVersion = 0
        private set

    override fun initialize(data: ContextConfigurationData) {
        check(handle == null) { "Audio context already initialized" }
        val device = checkNotNull(alcOpenDevice(null)) { "Unable to open an OpenAL device" }
        handle = device
        try {
            val created = checkNotNull(alcCreateContext(device, null)) { "Unable to create OpenAL context" }
            context = created
            check(alcMakeContextCurrent(created).toInt() != 0) { "Unable to activate OpenAL context" }
            memScoped {
                val major = alloc<IntVar>(); val minor = alloc<IntVar>()
                alcGetIntegerv(device, ALC_MAJOR_VERSION, 1, major.ptr)
                alcGetIntegerv(device, ALC_MINOR_VERSION, 1, minor.ptr)
                majorVersion = major.value; minorVersion = minor.value
            }
        } catch (error: Throwable) {
            destroy()
            throw error
        }
    }
    override fun destroy() {
        context?.let { alcMakeContextCurrent(null); alcDestroyContext(it) }
        context = null
        handle?.let { alcCloseDevice(it) }
        handle = null
    }
}

private class AudioVector(private val read: () -> FloatArray, private val write: (FloatArray) -> Unit) : MutableVector3 {
    override var x: Double
        get() = read()[0].toDouble()
        set(value) { set(value, y, z) }
    override var y: Double
        get() = read()[1].toDouble()
        set(value) { set(x, value, z) }
    override var z: Double
        get() = read()[2].toDouble()
        set(value) { set(x, y, value) }
    override fun set(x: Double, y: Double, z: Double) = write(floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat()))
}

private class NativeListener : Listener {
    override var gain: Float
        get() = memScoped { val v = alloc<FloatVar>(); alGetListenerf(AL_GAIN, v.ptr); v.value }
        set(value) { alListenerf(AL_GAIN, value) }
    private fun vector(property: Int) = AudioVector(
        read = { FloatArray(3).also { it.usePinned { p -> alGetListenerfv(property, p.addressOf(0)) } } },
        write = { it.usePinned { p -> alListenerfv(property, p.addressOf(0)) } },
    )
    private val pos = vector(AL_POSITION)
    private val vel = vector(AL_VELOCITY)
    override var position: MutableVector3
        get() = pos
        set(value) { pos.set(value.x, value.y, value.z) }
    override var velocity: MutableVector3
        get() = vel
        set(value) { vel.set(value.x, value.y, value.z) }
    override var distanceModel = Listener.DistanceModel.INVERSE
        set(value) {
            val model = when (value) {
                Listener.DistanceModel.INVERSE -> if (value.clamped) AL_INVERSE_DISTANCE_CLAMPED else AL_INVERSE_DISTANCE
                Listener.DistanceModel.LINEAR -> if (value.clamped) AL_LINEAR_DISTANCE_CLAMPED else AL_LINEAR_DISTANCE
                Listener.DistanceModel.EXPONENTIAL -> if (value.clamped) AL_EXPONENT_DISTANCE_CLAMPED else AL_EXPONENT_DISTANCE
            }
            alDistanceModel(model)
            field = value
        }
    override val orientation = object : Listener.Orientaion {
        private fun vector(offset: Int) = AudioVector(
            read = {
                val all = FloatArray(6)
                all.usePinned { alGetListenerfv(AL_ORIENTATION, it.addressOf(0)) }
                all.copyOfRange(offset, offset + 3)
            },
            write = { data ->
                val all = FloatArray(6)
                all.usePinned { alGetListenerfv(AL_ORIENTATION, it.addressOf(0)) }
                data.copyInto(all, offset)
                all.usePinned { alListenerfv(AL_ORIENTATION, it.addressOf(0)) }
            },
        )
        private val atVector = vector(0)
        private val upVector = vector(3)
        override var at: Vector3
            get() = atVector
            set(value) { atVector.set(value.x, value.y, value.z) }
        override var up: Vector3
            get() = upVector
            set(value) { upVector.set(value.x, value.y, value.z) }
    }
}

private class NativeSoundSource(override val sound: Sound) : SoundSource {
    override val id: Int = memScoped {
        val source = alloc<UIntVar>()
        alGetError(); alGenSources(1, source.ptr)
        check(source.value != 0u && alGetError() == AL_NO_ERROR) { "Unable to create audio source" }
        alSourcei(source.value, AL_BUFFER, sound.id)
        source.value.toInt()
    }
    private var deleted = false
    private fun getFloat(property: Int) = memScoped { val v = alloc<FloatVar>(); alGetSourcef(id.toUInt(), property, v.ptr); v.value }
    private fun setFloat(property: Int, value: Float) = alSourcef(id.toUInt(), property, value)
    private fun getInt(property: Int) = memScoped { val v = alloc<IntVar>(); alGetSourcei(id.toUInt(), property, v.ptr); v.value }
    private fun vector(property: Int) = AudioVector(
        read = { FloatArray(3).also { it.usePinned { p -> alGetSourcefv(id.toUInt(), property, p.addressOf(0)) } } },
        write = { it.usePinned { p -> alSourcefv(id.toUInt(), property, p.addressOf(0)) } },
    )
    private val pos = vector(AL_POSITION)
    private val vel = vector(AL_VELOCITY)
    override var position: MutableVector3
        get() = pos
        set(value) { pos.set(value.x, value.y, value.z) }
    override var velocity: MutableVector3
        get() = vel
        set(value) { vel.set(value.x, value.y, value.z) }
    override var gain: Float get() = getFloat(AL_GAIN); set(value) = setFloat(AL_GAIN, value)
    override var pitch: Float get() = getFloat(AL_PITCH); set(value) = setFloat(AL_PITCH, value)
    override var rolloff: Float get() = getFloat(AL_ROLLOFF_FACTOR); set(value) = setFloat(AL_ROLLOFF_FACTOR, value)
    override var maxDistance: Float get() = getFloat(AL_MAX_DISTANCE); set(value) = setFloat(AL_MAX_DISTANCE, value)
    override var referenceDistance: Float get() = getFloat(AL_REFERENCE_DISTANCE); set(value) = setFloat(AL_REFERENCE_DISTANCE, value)
    override var looped: Boolean get() = getInt(AL_LOOPING) != 0; set(value) = alSourcei(id.toUInt(), AL_LOOPING, if (value) 1 else 0)
    override val isPlaying: Boolean get() = getInt(AL_SOURCE_STATE) == AL_PLAYING
    override var progress: Float
        get() = getInt(AL_SAMPLE_OFFSET).toFloat() / sound.sampleRate
        set(value) { alSourcei(id.toUInt(), AL_SAMPLE_OFFSET, (value * sound.sampleRate).roundToInt()) }
    override fun play() { if (!deleted) alSourcePlay(id.toUInt()) }
    override fun pause() { if (!deleted) alSourcePause(id.toUInt()) }
    override fun stop() { if (!deleted) alSourceStop(id.toUInt()) }
    override fun destroy() {
        if (!deleted) memScoped {
            deleted = true
            alSourcei(id.toUInt(), AL_BUFFER, 0)
            val source = alloc<UIntVar>(); source.value = id.toUInt()
            alDeleteSources(1, source.ptr)
        }
    }
}

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.context.loader.BackgroundTask
import com.mechanica.engine.context.loader.MiscFactory
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicReference
import kotlin.reflect.KProperty
import platform.posix.*

class NativeMiscFactory(private val persistencePath: String) : MiscFactory {
    override fun persistencePropertyName(owner: Any?, property: KProperty<*>): String =
        "${owner?.let { it::class.qualifiedName } ?: "static"}.${property.name}"
    override fun defaultPersistencePath(): String = persistencePath

    override fun startBackgroundTask(task: () -> Unit): BackgroundTask {
        val result = NativeBackgroundTask(task)
        val ref = StableRef.create(result)
        val error = memScoped {
            val thread = alloc<pthread_tVar>()
            val status = pthread_create(thread.ptr, null, staticCFunction { pointer: COpaquePointer? ->
                val stable = pointer!!.asStableRef<NativeBackgroundTask>()
                val work = stable.get()
                try { work.action() } catch (error: Throwable) { work.error.store(error) }
                finally { work.done.store(1); stable.dispose() }
                null
            }, ref.asCPointer())
            if (status == 0) pthread_detach(thread.value)
            status
        }
        if (error != 0) {
            ref.dispose()
            error("Unable to start background thread: $error")
        }
        return result
    }
    override fun enableAlphaBlending() {
        glEnable(GL_BLEND.toUInt())
        glBlendFunc(GL_SRC_ALPHA.toUInt(), GL_ONE_MINUS_SRC_ALPHA.toUInt())
    }
    override fun disableColor() = glColorMask(GL_FALSE.toUByte(), GL_FALSE.toUByte(), GL_FALSE.toUByte(), GL_FALSE.toUByte())
    override fun enableColor() = glColorMask(GL_TRUE.toUByte(), GL_TRUE.toUByte(), GL_TRUE.toUByte(), GL_TRUE.toUByte())
}

private class NativeBackgroundTask(val action: () -> Unit) : BackgroundTask {
    val done = AtomicInt(0)
    val error = AtomicReference<Throwable?>(null)
    override val isFinished: Boolean get() = done.load() != 0
    override val failure: Throwable? get() = error.load()
}

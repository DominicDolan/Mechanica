package com.mechanica.engine.context.loader

import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import com.mechanica.engine.util.getCallingClass
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL20

class LwjglMiscFactory : MiscFactory {
    override fun persistencePropertyName(owner: Any?, property: KProperty<*>): String {
        val ownerName = if (owner != null) owner::class.java.canonicalName
            else property.javaField?.declaringClass?.canonicalName ?: "static"
        return "$ownerName.${property.name}"
    }

    override fun defaultPersistencePath(): String = getCallingClass().`package`.name + "/res/data/persistence.json"

    override fun startBackgroundTask(task: () -> Unit): BackgroundTask {
        val result = object : BackgroundTask {
            @Volatile override var isFinished = false
            override var failure: Throwable? = null
        }
        Thread {
            try { task() } catch (error: Throwable) { result.failure = error }
            finally { result.isFinished = true }
        }.start()
        return result
    }

    override fun enableAlphaBlending() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun disableColor() {
        GL20.glColorMask(false, false, false, false)
    }

    override fun enableColor() {
        GL20.glColorMask(true, true, true, true)
    }
}
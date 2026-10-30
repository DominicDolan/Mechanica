package com.mechanica.engine.context.loader

import kotlin.reflect.KProperty

interface BackgroundTask {
    val isFinished: Boolean
    val failure: Throwable?
}

interface MiscFactory {
    fun persistencePropertyName(owner: Any?, property: KProperty<*>): String
    fun defaultPersistencePath(): String
    fun startBackgroundTask(task: () -> Unit): BackgroundTask
    fun enableAlphaBlending()
    fun disableColor()
    fun enableColor()
}
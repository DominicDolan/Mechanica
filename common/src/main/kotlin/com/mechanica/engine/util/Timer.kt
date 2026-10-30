@file:Suppress("unused") // There will be functions here that go unused most of the time
package com.mechanica.engine.util

import kotlin.time.TimeSource

object Timer {
    private val origin = TimeSource.Monotonic.markNow()
    private val ids = HashMap<Int, Double>()
    val now get() = origin.elapsedNow().inWholeNanoseconds / 1e9
    val begin = now
    val elapsed get() = now - begin

    fun start(id: Int) {
        ids[id] = now
    }

    fun end(id: Int): Double {
        val start = ids[id]
        return if (start != null) {
            now - start
        } else -1.0
    }
}
package com.mechanica.engine.debug

interface DebugConfiguration {
    val debugMode: Boolean
    val failEarly: Boolean
    val screenLog: Boolean
    val constructionDraws: Boolean
    val printWarnings: Boolean
    val lwjglDebug: Boolean
    val isPaused: Boolean
    fun pauseUpdates(pause:  Boolean)
    fun frameAdvance()

    /** Advances a paused game by [frames] fixed size steps, one per rendered frame. */
    fun stepFrames(frames: Int)
}
package com.mechanica.engine.debug

interface DebugConfiguration {
    val debugMode: Boolean
    val failEarly: Boolean
    val screenLog: Boolean
    val constructionDraws: Boolean
    val printWarnings: Boolean
    val lwjglDebug: Boolean
    fun pauseUpdates(pause:  Boolean)
    fun frameAdvance()
}
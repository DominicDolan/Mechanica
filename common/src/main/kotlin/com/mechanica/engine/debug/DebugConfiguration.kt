package com.mechanica.engine.debug

interface DebugConfiguration {
    val failEarly: Boolean
    val screenLog: Boolean
    val constructionDraws: Boolean
    val printWarnings: Boolean
    val lwjglDebug: Boolean
}
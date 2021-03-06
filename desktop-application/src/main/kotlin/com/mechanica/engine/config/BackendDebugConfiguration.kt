package com.mechanica.engine.config

import com.mechanica.engine.debug.DebugConfiguration

object BackendDebugConfiguration : DebugConfiguration {
    private var configuration: DebugConfiguration? = null

    fun set(configuration: DebugConfiguration) {
        this.configuration = configuration
    }

    override val debugMode: Boolean
        get() = configuration?.debugMode ?: false

    override val failEarly: Boolean
        get() = configuration?.failEarly ?: false
    override val screenLog: Boolean
        get() = configuration?.screenLog ?: false
    override val constructionDraws: Boolean
        get() = configuration?.constructionDraws ?: false
    override val printWarnings: Boolean
        get() = configuration?.printWarnings ?: false
    override val lwjglDebug: Boolean
        get() = configuration?.lwjglDebug ?: false

    override fun pauseUpdates(pause: Boolean) {
        configuration?.pauseUpdates(pause)
    }

    override fun frameAdvance() {
        configuration?.frameAdvance()
    }
}
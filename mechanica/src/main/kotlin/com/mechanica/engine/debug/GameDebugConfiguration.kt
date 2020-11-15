package com.mechanica.engine.debug

import com.mechanica.engine.game.Game

class GameDebugConfiguration : DebugConfiguration {
    override var debugMode: Boolean = true
    override var failEarly = true
        get() = field && debugMode
    override var screenLog = true
        get() = field && debugMode
    override var constructionDraws = true
        get() = field && debugMode
    override var printWarnings = true
        get() = field && debugMode
    override var lwjglDebug = true
        get() = field && debugMode

    override fun pauseUpdates(pause: Boolean) {
        Game.sceneManager.pauseExecution(pause)
    }

    override fun frameAdvance() {
        Game.sceneManager.frameAdvance()
    }
}
package com.mechanica.engine.debug

import com.mechanica.engine.game.Game

class GameDebugConfiguration : DebugConfiguration {
    override var failEarly = false
    override var screenLog = false
    override var constructionDraws = false
    override var printWarnings = false
    override var lwjglDebug = false

    override fun pauseUpdates(pause: Boolean) {
        Game.sceneManager.pauseExecution(pause)
    }

    override fun frameAdvance() {
        Game.sceneManager.frameAdvance()
    }
}
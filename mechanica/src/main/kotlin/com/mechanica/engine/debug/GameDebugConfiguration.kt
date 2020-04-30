package debug

import com.mechanica.engine.debug.DebugConfiguration

class GameDebugConfiguration : DebugConfiguration {
    override var failEarly = false
    override var screenLog = false
    override var constructionDraws = false
    override var printWarnings = false
    override var lwjglDebug = false
}
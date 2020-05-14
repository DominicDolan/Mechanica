package debug

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Keyboard

object ScreenLog {
    private val sb = StringBuilder()
    internal fun render(draw: Drawer) {
        val v = Game.ui
        val fontSize = v.height*0.02
        draw.ui.layout.origin(0, 1).green.alpha(0.5).text(sb.toString(), fontSize, v.left, v.top)
        sb.clear()
    }

    fun addLine(text: String) {
        sb.appendln(text)
    }

    inline operator fun invoke(text: () -> String) {
        if (Game.debug.screenLog) {
            addLine(text())
        }
    }
}
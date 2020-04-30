package debug

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Keyboard

object ScreenLog {
    private val sb = StringBuilder()
    internal fun render(draw: Drawer) {
        if (Game.debug.screenLog) {
            val v = if (Keyboard.space()) {
                draw.ui
                Game.ui
            } else {
                draw.world
                Game.view
            }
            val fontSize = Game.view.height*0.02
            draw.green.alpha(0.5).text(sb.toString(), fontSize, v.left, v.top - fontSize)
            sb.clear()
        }
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
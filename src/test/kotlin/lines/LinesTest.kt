package lines

import demo.paths.DrawingExample
import game.Game
import input.Keyboard
import kotlin.test.Test

class LinesTest {
    @Test
    fun linesApp() {
        Game.configure {
            setViewport(height = 10.0)
            setResolution(1500, 1500)
            setStartingState { LinesExample() }
            configureWindow {
                isDecorated = false
            }
        }

        Game.run { if (Keyboard.ESC.hasBeenPressed) Game.close() }
    }
}
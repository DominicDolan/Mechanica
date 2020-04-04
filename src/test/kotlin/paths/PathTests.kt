package paths

import demo.paths.DrawingExample
import demo.paths.BezierCurves
import game.Game
import kotlin.test.Test

class PathTests {
    @Test fun drawingApp() {
        Game.configure {
            setViewport(height = 10.0)
            setStartingState { DrawingExample() }
        }
        Game.run()
    }

    @Test fun bezierCurveExample() {
        Game.configure {
            setViewport(height = 10.0)
            setStartingState { BezierCurves() }
        }
        Game.run()
    }
}
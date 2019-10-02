package state

import display.Game
import renderer.Painter

/**
 * Created by domin on 4 Aug 2017.
 */
abstract class LoadState : State() {
    private val waitTime = 0.2f
    private val minLoops = 2

    private var currentLoops = 0
    private var currentWait = 0.0
    private var startLoading = false
    private var finishedLoading = false
    var startingState: () -> State = { EmptyState }

    override fun update(delta: Double) {
        currentWait += delta
        currentLoops++
        startLoading = currentWait > waitTime && currentLoops > minLoops
        if (finishedLoading) onFinish()

    }

    override fun render(g: Painter) {
        renderLoadScreen(g)
        if (startLoading) {
            load()
            finishedLoading = true
        }

    }

    abstract fun preLoad()
    abstract fun renderLoadScreen(g: Painter)

    abstract fun load()

    private fun onFinish(){
        Game.setCurrentState(startingState)
    }
}

package state

import drawer.Drawer

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

    override fun update(delta: Double) {
        currentWait += delta
        currentLoops++
        startLoading = currentWait > waitTime && currentLoops > minLoops
        if (finishedLoading) onFinish()

    }

    override fun render(draw: Drawer) {
        renderLoadScreen(draw)
        if (startLoading) {
            load()
            finishedLoading = true
        }

    }

    abstract fun preLoad()
    abstract fun renderLoadScreen(g: Drawer)

    abstract fun load()

    internal var onFinish: () -> Unit = { }
}

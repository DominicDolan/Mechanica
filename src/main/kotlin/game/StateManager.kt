package game

import drawer.Drawer
import drawer.DrawerImpl
import game.configuration.ConfigurationData
import state.State
import util.Timer

internal class StateManager {

    private var stateSetter: () -> State = { ConfigurationData.emptyState() }
    private var currentState: State = ConfigurationData.emptyState()
    private var scheduleStateChange = true

    private val drawer: Drawer by lazy { DrawerImpl() }

    var startOfLoop = Timer.now
    var updateDuration = 0.1

    fun updateState() {
        updateDuration = Timer.now - startOfLoop
        startOfLoop = Timer.now

        if (scheduleStateChange) {
            currentState = stateSetter()
            scheduleStateChange = false
        }

        currentState.update(updateDuration)
        currentState.render(drawer)

    }

    fun setCurrentState(setter: () -> State) {
        stateSetter = setter
        scheduleStateChange = true
    }

}
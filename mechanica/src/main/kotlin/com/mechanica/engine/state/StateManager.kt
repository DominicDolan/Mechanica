package com.mechanica.engine.state

import com.mechanica.engine.debug.DebugDrawer
import debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.ConfigurationData
import com.mechanica.engine.util.Timer

internal class StateManager {

    private var stateSetter: () -> State? = { null }
    private var currentState: State? = null
    private var scheduleStateChange = true

    private val drawer: Drawer by lazy { Drawer.create() }

    private var startOfLoop = Timer.now
    private var updateDuration = 0.1

    fun updateState() {
        updateDuration = Timer.now - startOfLoop
        startOfLoop = Timer.now

        if (scheduleStateChange) {
            currentState = stateSetter()
            scheduleStateChange = false
        }

        currentState?.update(updateDuration)
        currentState?.render(drawer)

        if (Game.debug.screenLog)
            ScreenLog.render(drawer)
        if (Game.debug.constructionDraws)
            DebugDrawer.render(drawer)
    }

    fun setCurrentState(setter: () -> State?) {
        stateSetter = setter
        scheduleStateChange = true
    }

}
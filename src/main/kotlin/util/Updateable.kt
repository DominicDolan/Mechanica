package util

import display.Game
import state.State

interface Updateable {
    fun update(delta: Double)

    fun continueUpdatingForCurrentState() {
        Game.addItemToUpdateForCurrentState(this)
    }

    fun continueUpdatingUntilStopped() {
        Game.addItemToUpdate(this)
    }

    fun stopUpdating() {
        Game.stopItemUpdating(this)
    }
}
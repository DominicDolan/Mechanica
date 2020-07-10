package com.mechanica.engine.ui

import com.dubulduke.ui.event.Event
import com.dubulduke.ui.render.RenderDescription
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.util.EventBoolean

class Events(description: RenderDescription<Style>) : Event(description) {
    private val booleans = ArrayList<EventBoolean>()

    override fun update() {
        for (i in booleans.indices) {
            booleans[i].update()
        }
    }

    val hoverEvent = UIEventBoolean { description.contains(Mouse.ui.x, Mouse.ui.y) }

    private val click = UIEventBoolean { Mouse.MB1() }

    val wasClicked: Boolean
        get() = click.hasBeenTrue && hoverEvent()

    val clickReleased: Boolean
        get() = click.hasBeenFalse && hoverEvent()

    val isHovering: Boolean
        get() = hoverEvent()

    inline fun ifHovering(action: () -> Unit) {
        if (isHovering) action()
    }

    inline fun ifClicked(action: () -> Unit) {
        if (wasClicked) action()
    }

    private var trackClicked = false
    val clickedAndReleased: Boolean
        get() {
            if (wasClicked) trackClicked = true

            if (click.hasBeenFalse) {
                val trackClicked = trackClicked
                this.trackClicked = false
                if (trackClicked) {
                    return hoverEvent()
                }
            }

            return false
        }

    inline fun ifClickedAndReleased(action: () -> Unit) {
        if (clickedAndReleased) {
            action()
        }
    }


    inner class UIEventBoolean(condition: () -> Boolean) : EventBoolean(condition) {
        init {
            booleans.add(this)
        }
    }
}
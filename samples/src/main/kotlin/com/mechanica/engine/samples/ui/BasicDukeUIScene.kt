package com.mechanica.engine.samples.ui

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.Camera
import com.mechanica.engine.samples.ui.duke.DukeUI
import com.mechanica.engine.samples.ui.duke.DukeUI.DukeBuilder
import com.mechanica.engine.samples.ui.duke.builder.NodeBuilder
import com.mechanica.engine.samples.ui.duke.builder.ContentBuilder
import com.mechanica.engine.samples.ui.duke.context.DukeContext
import com.mechanica.engine.samples.ui.duke.context.DukeViewport
import com.mechanica.engine.samples.ui.duke.context.DukeWindow
import com.mechanica.engine.samples.ui.duke.context.RenderDescription
import com.mechanica.engine.samples.ui.duke.h
import com.mechanica.engine.samples.ui.duke.layout.alignLeft
import com.mechanica.engine.samples.ui.duke.layout.alignTop
import com.mechanica.engine.samples.ui.duke.layout.width
import com.mechanica.engine.samples.ui.duke.theme.DukeTheme
import com.mechanica.engine.samples.ui.duke.theme.buildTheme


class MechanicaDukeContext(camera: Camera) : DukeContext() {
    override val window = DukeWindow.create(-camera.width/2.0, -camera.height/2.0, camera.width, camera.height)
    override val viewport = DukeViewport.create(0.0, 0.0, camera.width, -camera.height)

    private var drawer: Drawer? = null
    override fun renderElement(renderContext: RenderDescription) {
        val draw = drawer
        require(draw != null) { "Tried to draw UI but the Drawer is null! use setDrawer before running UI" }

        draw.ui.color(renderContext.style.color).rectangle(renderContext.x, renderContext.y, renderContext.width, renderContext.height)
    }

    override val theme = buildTheme {
        hook("app") {
            color.set(0x00FF00FF)

            hook("listItem") {
                color.set(0xFF00FFFF)
            }
        }
    }

    fun setDrawer(drawer: Drawer) {
        this.drawer = drawer
    }
}

class BasicDukeUIScene : Scene() {
    val uiContext: MechanicaDukeContext = MechanicaDukeContext(Game.ui)
    val uiEngine: DukeUI = DukeUI(uiContext)

    override fun render(draw: Drawer) {
        uiContext.setDrawer(draw)
        uiEngine.render()
    }

    override fun update(delta: Double) {
        uiEngine.build {

            h().layout().alignTop(1.0).width(5.0).alignLeft(3.0).edit { p, s ->
                height = p.height/2.0
            }.style().theme("app").content {
                h()
            }
        }
    }
}


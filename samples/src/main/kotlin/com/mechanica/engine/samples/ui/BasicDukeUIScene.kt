package com.mechanica.engine.samples.ui

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.Camera
import com.mechanica.engine.samples.ui.duke.DukeUI
import com.mechanica.engine.samples.ui.duke.component.ComponentBuilder
import com.mechanica.engine.samples.ui.duke.component.ContentBuilder
import com.mechanica.engine.samples.ui.duke.context.DukeContext
import com.mechanica.engine.samples.ui.duke.context.DukeViewport
import com.mechanica.engine.samples.ui.duke.context.DukeWindow
import com.mechanica.engine.samples.ui.duke.context.RenderDescription


class MechanicaDukeContext(camera: Camera) : DukeContext() {
    override val window = DukeWindow.create(-camera.width/2.0, -camera.height/2.0, camera.width, camera.height)
    override val viewport = DukeViewport.create(0.0, 0.0, camera.width, -camera.height)

    private var drawer: Drawer? = null
    override fun renderElement(renderContext: RenderDescription) {
        val draw = drawer
        require(draw != null) { "Tried to draw UI but the Drawer is null! use setDrawer before running UI" }

        draw.ui.color(renderContext.style.color).rectangle(renderContext.x, renderContext.y, renderContext.width, renderContext.height)
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
            e.layout.edit { p, s ->
                top = p.top + 1.0
                width = 5.0
                height = p.height/2.0
                left = p.left + 3.0
            }.style.edit {
                color.set(0x00FF00FF)
            }.content {
                e.layout.edit { p, s ->
                    center.set(p.center)
                    width = p.width/2.0
                    height = p.height/2.0
                }.style.edit {
                    color.set(0x0000FFFF)
                }

                e.layout.edit { p, s ->
                    top = s.bottom
                    height = 1.0

                    left = p.left
                    width = p.width
                }.style.edit {
                    color.set(0xFFFF00FF)
                }
            }
        }
    }
}

val ContentBuilder.e: ComponentBuilder
    get() = appendComponent()

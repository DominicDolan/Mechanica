package demo.nanovg

import display.Game
import display.GameOptions
import drawer.Drawer
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL2.*
import org.lwjgl.opengl.GL11.GL_STENCIL_TEST
import org.lwjgl.opengl.GL11.glEnable
import state.State


fun main() {
    val options = GameOptions()
            .setResolution(1920, 1080)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    val vg: Long
    val color = NVGColor.create()

    init {
        glEnable(GL_STENCIL_TEST);
        vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
    }

    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        nvgBeginFrame(vg, 200f, 100f, 1f)
        nvgBeginPath(vg);
        nvgRect(vg, 10f,30f, 120f,30f);
        nvgCircle(vg, 60f,60f, 20f);
        nvgPathWinding(vg, NVG_HOLE);	// Mark circle as a hole.
        nvgFillColor(vg, nvgRGBA(255.toByte(),192.toByte(),0,255.toByte(), color));
        nvgFill(vg);
        nvgEndFrame(vg)
    }
}
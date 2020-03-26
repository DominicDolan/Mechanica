package ui

//import com.dubulduke.ui.DynamicUI
//import com.dubulduke.ui.DynamicUIOptions
//import com.dubulduke.ui.element.Element
//import com.dubulduke.ui.element.box
//import com.dubulduke.ui.input.MouseCallback
//import com.dubulduke.ui.render.RenderDescription
import display.Game
import display.GameOptions
import drawer.Drawer
import input.Cursor
import input.Keyboard
import org.lwjgl.nuklear.NkContext
import org.lwjgl.nuklear.NkRect
import org.lwjgl.nuklear.Nuklear.*
import state.State

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}


private class StartMain : State() {
//    val ui: DynamicUI<Drawer> = createMechanicaUI()
    private val ctx = NkContext.create()
    private val rect = NkRect.create()

    enum class Type {EASY, HARD}
    var op = Type.EASY
    val value = 0.6f
    val i =  20

    override fun update(delta: Double) {
        if(nk_begin(ctx, "Show", nk_rect(50f, 50f, 220f, 220f, rect),
        NK_WINDOW_BORDER or NK_WINDOW_MOVABLE or NK_WINDOW_CLOSABLE)) {
            /* fixed widget pixel width */
            nk_layout_row_static(ctx, 30f, 80, 1)
            if (nk_button_label(ctx, "button")) {
                /* event handling */
            }

            /* fixed widget window ratio width */
            nk_layout_row_dynamic(ctx, 30f, 2)
            if (nk_option_label(ctx, "easy", op == Type.EASY)) op = Type.EASY
            if (nk_option_label(ctx, "hard", op == Type.HARD)) op = Type.HARD

            /* custom widget pixel width */
            nk_layout_row_begin(ctx, NK_STATIC, 30f, 2)

            nk_layout_row_push(ctx, 50f)
            nk_label(ctx, "Volume:", NK_TEXT_LEFT)
            nk_layout_row_push(ctx, 110f)
            nk_slider_float(ctx, 0f, floatArrayOf(value), 1.0f, 0.1f)

            nk_layout_row_end(ctx)
        }
        nk_end(ctx)
    }

    override fun render(draw: Drawer) {
//        ui(draw) {
////            box {
////                style { color = 0x445070FF }
////                layout { p, s ->
////                    this.left = p.left + 2.0
////                    this.right = p.right - 2.0
////                }
////                button {
////                    layout { p, s ->
////                        this.top = p.top + 2
////                    }
////
////                }
////            }
////        }
    }

}

//fun Element.button(block: Element.() -> Unit) {
//    val button = addChild()
//    button.style {
//        color = 0xFF00FFFF
//    }
//    button.layout { p, s ->
//        width = 3.0
//        height = 1.0
//    }
//    block(button)
//}
//
//fun createMechanicaUI(): DynamicUI<Drawer> {
//
//    val callback = object : MouseCallback {
//        override val mouseX: Double
//            get() = Cursor.viewX
//        override val mouseY: Double
//            get() = Cursor.viewY
//        override val leftMouseButton: Boolean
//            get() = Keyboard.MB1()
//        override val rightMouseButton: Boolean
//            get() = Keyboard.MB2()
//
//    }
//
//    val drawElement: RenderDescription.(Drawer) -> Unit = { draw: Drawer ->
//        draw.ui.color(color).rectangle(x, y, width, height)
//        if (text.isNotBlank()) {
//            draw.ui.black.text(text, fontSize, x, y)
//        }
//    }
//
//    val options = DynamicUIOptions(drawElement)
//            .setWindow(-Game.viewWidth/2.0, -Game.viewHeight/2.0, Game.viewWidth, Game.viewHeight, yDirection = DynamicUIOptions.YDirection.UP)
//            .setViewport(0.0, 0.0, Game.viewWidth, -Game.viewHeight)
//            .setMouseCallback(callback)
//
//    return DynamicUI(options)
//}
package com.mechanica.engine.scenes.scenes.logo

import com.cave.library.color.Color
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.View
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.resources.defaults
import com.mechanica.engine.scenes.addAnimationSequence
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.models.ImageModel
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.Text
import kotlin.math.min

class MechanicaStartupScreen(private val textColor: Color = Color.black) : Scene() {
    private val titleSize: Double = min(Game.ui.width, Game.ui.height)
    private val title = ImageModel(Image.create(Res.image("logo/mechanica-title")))

    private val poweredBy = Text("Powered by", Font.defaults.bold(true))

    private val logo = addScene(object : MechanicaLoadingIcon(0.0, -0.075*titleSize, 0.37*titleSize, 0.37*titleSize){
        override fun drawInView(draw: Drawer, view: View): Drawer {
            return super.drawInView(draw, view).ui.color.alpha(logoFadeIn.value*alpha)
        }
    }, 1)

    private val textFadeIn = AnimationFormula(2.0, 4.0, AnimationFormulas.linear(0.0, 1.0) )
    private val logoFadeIn = AnimationFormula(-1.0, 1.0, AnimationFormulas.linear(0.0, 1.0) )
    val sequence = addAnimationSequence(textFadeIn, logoFadeIn, logo.sequence)

    var alpha = 1.0

    init {
        logo.sequence.looped = false
        sequence.play()
    }

    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {
        draw.darkGrey.alpha(textFadeIn.value*alpha).text(poweredBy, 0.04*titleSize, -0.47*titleSize, 0.225*titleSize)
        draw.color(textColor).alpha(textFadeIn.value*alpha)
        draw.centered.ui.transformed.scale(titleSize).model(title, 0f, 1f, true)
    }
}
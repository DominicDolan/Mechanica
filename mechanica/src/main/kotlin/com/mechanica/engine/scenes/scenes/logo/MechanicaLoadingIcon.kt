package com.mechanica.engine.scenes.scenes.logo

import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.scenes.addAnimationSequence
import com.mechanica.engine.scenes.scenes.sprites.StaticSprite
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.unit.angle.degrees

open class MechanicaLoadingIcon(view: View, order: Int = 0) : StaticSprite(view, order) {
    private val mother = Image.create(Res.image("logo/astrolabe-mother"))
    private val rete = Image.create(Res.image("logo/astrolabe-rete"))
    private val rule = Image.create(Res.image("logo/astrolabe-rule"))

    private var buffer = AnimationFormula(-1.0, 0.0) { 1.0 }
    private var ruleAngle = AnimationFormula(0.0, 2.5, AnimationFormulas.quadraticBump(0.0, 360.0))
    private var reteAngle = AnimationFormula(0.0, 2.5, AnimationFormulas.quadraticAscending(360.0, 0.0))

    val sequence = addAnimationSequence(buffer, reteAngle, ruleAngle)

    constructor(
            x: Double = 0.0,
            y: Double = 0.0,
            width: Double = 1.0,
            height: Double = 1.0,
            order: Int = 0) : this(View.create(x, y, width, height), order)

    init {
        sequence.play()
        sequence.looped = true
    }

    override fun render(draw: Drawer) {
        draw.inView.image(mother)
        draw.inView.rotated(reteAngle.value.degrees).image(rete)
        draw.inView.rotated(ruleAngle.value.degrees).image(rule)
    }
}
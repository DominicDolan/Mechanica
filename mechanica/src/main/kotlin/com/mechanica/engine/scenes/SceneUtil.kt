package com.mechanica.engine.scenes

import com.mechanica.engine.animation.AnimationController
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.animation.AnimationSequence
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.SceneNode


fun SceneHub.addAnimation(length: Double, formula: AnimationFormulas.(Double) -> Double): AnimationFormula {
    return addScene(AnimationFormula(0.0, length, formula))
}

fun SceneHub.addAnimationSequence(vararg animations: AnimationController): AnimationSequence {
    return addScene(AnimationSequence(*animations))
}

@Suppress("unused")
fun SceneHub.setNewMainScene(setter: () -> SceneNode?) = Game.setMainScene(setter)



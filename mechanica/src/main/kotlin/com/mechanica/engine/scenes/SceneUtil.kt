package com.mechanica.engine.scenes

import com.mechanica.engine.animation.AnimationController
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.animation.AnimationSequence
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap
import com.mechanica.engine.scenes.exclusiveScenes.NoneOrOneActivationMap
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.SceneNode


fun SceneHub.addAnimation(length: Double, formula: AnimationFormulas.(Double) -> Double): AnimationFormula {
    return addScene(AnimationFormula(0.0, length, formula))
}

fun SceneHub.addAnimationSequence(vararg animations: AnimationController): AnimationSequence {
    return addScene(AnimationSequence(*animations))
}


/**
 * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.addNoneOrOneActiveScenes] but it adds
 * scenes to the parent instead of processes
 *
 * @param scenes the set of scenes that will be added as children to this scene and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Scene> SceneHub.addNoneOrOneActiveScenes(vararg scenes: P): NoneOrOneActivationMap<P> {
    val parent = this
    return object : NoneOrOneActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addScene(process)
            return super.addProcess(process)
        }
    }
}

/**
 * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.addExclusivelyActiveScenes] but it adds
 * scenes to the parent instead of processes
 *
 * @param scenes the set of scenes that will be added as children to this scene and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Scene> SceneHub.addExclusivelyActiveScenes(vararg scenes: P): ExclusiveActivationMap<P> {
    val parent = this
    return object : ExclusiveActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addScene(process)
            return super.addProcess(process)
        }
    }
}

@Suppress("unused")
fun SceneHub.setNewMainScene(setter: () -> SceneNode?) = Game.setMainScene(setter)



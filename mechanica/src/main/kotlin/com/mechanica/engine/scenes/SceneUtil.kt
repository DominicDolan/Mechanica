package com.mechanica.engine.scenes

import com.mechanica.engine.animation.AnimationController
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.animation.AnimationSequence
import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap
import com.mechanica.engine.scenes.processes.Process
import com.mechanica.engine.scenes.processes.ProcessNode
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneNode


fun ProcessNode.addAnimation(start: Double, end: Double, formula: AnimationFormulas.(Double) -> Double): AnimationFormula {
    return addProcess(AnimationFormula(start, end, formula))
}

fun ProcessNode.addAnimation(length: Double, formula: AnimationFormulas.(Double) -> Double): AnimationFormula {
    return addProcess(AnimationFormula(length, formula))
}

fun ProcessNode.addAnimationSequence(vararg animations: AnimationController): AnimationSequence {
    return addProcess(AnimationSequence(*animations))
}


/**
 * Sets the [processes] in which only one process can be active at once. This exclusivity is automatically enforced
 *
 * Usage:
 * ```
 * val currentlyActiveProcess by exclusivelyActiveProcesses(Process1(), Process2(), Process3())
 * ```
 *
 * @param processes the set of processes that will be added as children to this process and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Process> ProcessNode.exclusivelyActiveProcesses(vararg processes: P): ExclusiveActivationMap<P> {
    val parent = this
    return object : ExclusiveActivationMap<P>(*processes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addProcess(process)
            return super.addProcess(process)
        }
    }
}

/**
 * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.exclusivelyActiveProcesses] but it adds
 * scenes to the parent instead of processes
 *
 * @param scenes the set of scenes that will be added as children to this scene and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Scene> SceneNode.exclusivelyActiveScenes(vararg scenes: P): ExclusiveActivationMap<P> {
    val parent = this
    return object : ExclusiveActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addScene(process)
            return super.addProcess(process)
        }
    }
}



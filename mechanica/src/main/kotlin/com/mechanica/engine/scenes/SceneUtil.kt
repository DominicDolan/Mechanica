package com.mechanica.engine.scenes

import com.mechanica.engine.animation.AnimationController
import com.mechanica.engine.animation.AnimationFormula
import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.animation.AnimationSequence
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap
import com.mechanica.engine.scenes.exclusiveScenes.NoneOrOneActivationMap
import com.mechanica.engine.scenes.processes.Process
import com.mechanica.engine.scenes.processes.ProcessNode
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneNode


fun ProcessNode.addAnimation(length: Double, formula: AnimationFormulas.(Double) -> Double): AnimationFormula {
    return addProcess(AnimationFormula(0.0, length, formula))
}

fun ProcessNode.addAnimationSequence(vararg animations: AnimationController): AnimationSequence {
    return addProcess(AnimationSequence(*animations))
}


/**
 * Sets the [processes] in which zero or one process can be active at once and not more. This exclusivity is automatically enforced
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
fun <P: Process> ProcessNode.addNoneOrOneActiveProcesses(vararg processes: P): NoneOrOneActivationMap<P> {
    val parent = this
    return object : NoneOrOneActivationMap<P>(*processes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addProcess(process)
            return super.addProcess(process)
        }
    }
}

/**
 * Sets the [processes] in which only one process can be active at once and one process is guaranteed to be active. This exclusivity is automatically enforced
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
fun <P: Process> ProcessNode.addExclusivelyActiveProcesses(vararg processes: P): ExclusiveActivationMap<P> {
    val parent = this
    return object : ExclusiveActivationMap<P>(*processes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addProcess(process)
            return super.addProcess(process)
        }
    }
}

/**
 * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.addNoneOrOneActiveProcesses] but it adds
 * scenes to the parent instead of processes
 *
 * @param scenes the set of scenes that will be added as children to this scene and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Scene> SceneNode.addNoneOrOneActiveScenes(vararg scenes: P): NoneOrOneActivationMap<P> {
    val parent = this
    return object : NoneOrOneActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addScene(process)
            return super.addProcess(process)
        }
    }
}

/**
 * This does the same as [exclusivelyActiveProcesses][com.mechanica.engine.scenes.addExclusivelyActiveProcesses] but it adds
 * scenes to the parent instead of processes
 *
 * @param scenes the set of scenes that will be added as children to this scene and only one can be
 *                  set to active at a time
 * @return An ExclusiveProcessMap which can have more processes added at a later stage
 */
fun <P: Scene> SceneNode.addExclusivelyActiveScenesNew(vararg scenes: P): ExclusiveActivationMap<P> {
    val parent = this
    return object : ExclusiveActivationMap<P>(*scenes) {
        override fun <R : P> addProcess(process: R): R {
            parent.addScene(process)
            return super.addProcess(process)
        }
    }
}

@Suppress("unused")
fun SceneNode.setNewMainScene(setter: () -> Scene?) = Game.setMainScene(setter)



package com.mechanica.engine.scenes.exclusiveScenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.scenes.scenes.Scene

class SceneStack(): Scene() {
    private val stack = ArrayDeque<Scene>()
    val activeScene: Scene?
        get() = stack.lastOrNull()
    fun <S : Scene> add(scene: S, onActivate: (scene: S) -> Unit = {}, onDeactivate: (scene: S) -> Unit = {}): Item<S> {
        return InnerItem(scene, { onActivate(scene) }, { onDeactivate(scene) })
    }

    fun <S : Scene> add(scene: S, activatable: SceneStackActivatable): Item<S> {
        return InnerItem(scene, activatable::onActivated, activatable::onDeactivated)
    }

    override fun update(delta: Double) {
        val activeScene = stack.lastOrNull()
        activeScene?.updateChildren(delta)
    }

    override fun render(draw: Drawer) {
        val activeScene = stack.lastOrNull()
        activeScene?.renderChildren(draw)
    }

    private inner class InnerItem<S : Scene>(
        override val scene: S,
        val onActivate: () -> Unit = {},
        val onDeactivate: () -> Unit = {}) : Item<S> {
        override val isActive: Boolean
            get() = stack.lastOrNull() == scene
        override val isAttached: Boolean
            get() = scene in stack

        override fun activate() {
            if (isActive) return

            if (isAttached) {
                stack.remove(scene)
            }

            stack.addLast(scene)
            onActivate()
        }

        override fun deactivate() {
            if (!isActive) return
            if (isAttached) {
                stack.remove(scene)
                onDeactivate()
            }

        }
    }

    interface Item<S : Scene> {
        val scene: S
        val isActive: Boolean
        val isAttached: Boolean

        fun activate()
        fun deactivate()
    }
}


package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.SceneNode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SceneRemovalTests {

    private class CountingNode : SceneNode {
        var removeCount = 0
            private set

        override fun onRemove() {
            removeCount++
        }
    }

    private class CountingHub : SceneHub() {
        var removeCount = 0
            private set

        override fun onRemove() {
            removeCount++
        }
    }

    @Test
    fun removeSceneNotifiesTheWholeSubtreeExactlyOnce() {
        val root = CountingHub()
        val branch = root.addScene(CountingHub())
        val leaf = branch.addScene(CountingNode())

        assertTrue(root.removeScene(branch), "removeScene should report that the scene was removed")

        assertEquals(1, branch.removeCount, "the removed hub should be notified exactly once")
        assertEquals(1, leaf.removeCount, "a descendant of the removed hub should be notified exactly once")
        assertFalse(root.hasScene(branch), "the removed hub should be detached from its parent")
        assertFalse(branch.hasChildren, "the removed hub should have released its children")
    }

    @Test
    fun removeChildrenIsIdempotent() {
        val root = CountingHub()
        val branch = root.addScene(CountingHub())
        val leaf = branch.addScene(CountingNode())

        root.removeChildren()
        root.removeChildren()

        assertEquals(1, branch.removeCount, "a child should not be notified again by a second removeChildren")
        assertEquals(1, leaf.removeCount, "a grandchild should not be notified again by a second removeChildren")
        assertFalse(root.hasChildren, "removeChildren should detach the children, not just notify them")
    }

    @Test
    fun tearingDownAndThenReplacingASceneNotifiesItOnlyOnce() {
        // Mirrors SceneManager.checkStateChange, which tears down the outgoing scene's
        // children before swapping the scene itself.
        val root = CountingHub()
        val scene = root.addScene(CountingHub())
        val leaf = scene.addScene(CountingNode())

        scene.removeChildren()
        val replacement = CountingHub()
        root.replaceScene(scene, replacement)

        assertEquals(1, leaf.removeCount, "the torn down child should not be notified a second time by replaceScene")
        assertEquals(1, scene.removeCount, "the replaced scene should be notified exactly once")
        assertEquals(0, replacement.removeCount, "the incoming scene should not be notified")
        assertTrue(root.hasScene(replacement), "the incoming scene should be attached")
    }

    @Test
    fun removingASceneThatIsNotAChildDoesNothing() {
        val root = CountingHub()
        val stranger = CountingNode()

        assertFalse(root.removeScene(stranger), "removing a non-child should report failure")
        assertEquals(0, stranger.removeCount, "a non-child should not be notified")
    }
}

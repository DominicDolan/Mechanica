package com.mechanica.engine.scenes

import com.mechanica.engine.scenes.scenes.SceneHub
import com.mechanica.engine.scenes.scenes.Updateable
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Covers the update half of [SceneHub]'s traversal. The render half is structurally identical
 * but needs a [com.mechanica.engine.drawer.Drawer], which needs a GL context.
 */
class SceneTraversalTests {

    private class RecordingHub(
        private val log: MutableList<String>,
        private val name: String,
        override var active: Boolean = true,
        override var playing: Boolean = true
    ) : SceneHub(), Updateable {
        override fun update(delta: Double) {
            log.add("$name.update")
        }

        override fun updateWhileInactive(delta: Double) {
            log.add("$name.whileInactive")
        }
    }

    private class RecordingLeaf(
        private val log: MutableList<String>,
        private val name: String,
        override var active: Boolean = true,
        override var playing: Boolean = true
    ) : Updateable {
        override fun update(delta: Double) {
            log.add("$name.update")
        }

        override fun updateWhileInactive(delta: Double) {
            log.add("$name.whileInactive")
        }
    }

    @Test
    fun aHubUpdatesBetweenItsNegativeAndNonNegativeOrderedChildren() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        root.addScene(RecordingLeaf(log, "early"), -1)
        root.addScene(RecordingLeaf(log, "late"), 1)

        root.updateChildren(0.016)

        assertEquals(listOf("early.update", "root.update", "late.update"), log,
            "children ordered before the hub should update first, then the hub, then the rest")
    }

    @Test
    fun anInactiveLeafGetsUpdateWhileInactiveInstead() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        root.addScene(RecordingLeaf(log, "leaf", active = false))

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "leaf.whileInactive"), log,
            "an inactive node should receive updateWhileInactive in place of update")
    }

    @Test
    fun anInactiveHubSkipsItsWholeSubtree() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        val branch = root.addScene(RecordingHub(log, "branch", active = false))
        branch.addScene(RecordingLeaf(log, "leaf"))

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "branch.whileInactive"), log,
            "an inactive hub should not traverse its children")
    }

    @Test
    fun aHubThatIsNotPlayingStillUpdatesItsChildren() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        val branch = root.addScene(RecordingHub(log, "branch", playing = false))
        branch.addScene(RecordingLeaf(log, "leaf"))

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "branch.whileInactive", "leaf.update"), log,
            "playing should gate only the hub's own update, not its subtree")
    }

    @Test
    fun aLeafThatIsNotPlayingGetsUpdateWhileInactive() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        root.addScene(RecordingLeaf(log, "leaf", playing = false))

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "leaf.whileInactive"), log,
            "a leaf that is not playing should receive updateWhileInactive")
    }

    /** A leaf that mutates its parent from inside its own update. */
    private class MutatingLeaf(
        private val log: MutableList<String>,
        private val name: String,
        private val onUpdate: () -> Unit
    ) : Updateable {
        override fun update(delta: Double) {
            log.add("$name.update")
            onUpdate()
        }
    }

    @Test
    fun removingASiblingDuringUpdateDoesNotSkipTheNextOne() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")

        val second = RecordingLeaf(log, "second")
        val third = RecordingLeaf(log, "third")
        root.addScene(MutatingLeaf(log, "first") { root.removeScene(second) }, 1)
        root.addScene(second, 2)
        root.addScene(third, 3)

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "first.update", "third.update"), log,
            "removing a sibling mid traversal should skip that sibling without skipping the next one")
    }

    @Test
    fun addingASceneDuringUpdateDoesNotDisturbTheRestOfTheWalk() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")

        val added = RecordingLeaf(log, "added")
        var hasAdded = false
        root.addScene(MutatingLeaf(log, "first") {
            if (!hasAdded) {
                hasAdded = true
                root.addScene(added, 5)
            }
        }, 1)
        root.addScene(RecordingLeaf(log, "second"), 2)

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "first.update", "second.update"), log,
            "a scene added mid traversal should wait for the next traversal")

        log.clear()
        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "first.update", "second.update", "added.update"), log,
            "the added scene should take part in the following traversal")
    }

    @Test
    fun aSceneRemovedDuringUpdateIsNotUpdatedAfterOnRemove() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")

        val doomed = RecordingLeaf(log, "doomed")
        root.addScene(MutatingLeaf(log, "first") { root.removeScene(doomed) }, 1)
        root.addScene(doomed, 2)

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "first.update"), log,
            "a scene removed mid traversal should not be updated afterwards")
    }

    @Test
    fun nestedHubsUpdateDepthFirstInOrder() {
        val log = ArrayList<String>()
        val root = RecordingHub(log, "root")
        val branch = root.addScene(RecordingHub(log, "branch"), 1)
        branch.addScene(RecordingLeaf(log, "before"), -1)
        branch.addScene(RecordingLeaf(log, "after"), 1)

        root.updateChildren(0.016)

        assertEquals(listOf("root.update", "before.update", "branch.update", "after.update"), log,
            "each hub should update between its own negative and non negative ordered children")
    }
}

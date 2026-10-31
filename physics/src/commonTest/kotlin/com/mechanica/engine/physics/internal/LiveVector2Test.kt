package com.mechanica.engine.physics.internal

import com.cave.library.vector.vec2.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LiveVector2Test {
    @Test
    fun viewTracksBackendAndWritesBothComponents() {
        var state = Vector2.create(1.0, 2.0)
        var writes = 0
        val view = LiveVector2({ state }) { x, y ->
            writes++
            state = Vector2.create(x, y)
        }

        state = Vector2.create(3.0, 4.0)
        assertEquals(3.0, view.x)
        view.x = 5.0
        assertEquals(5.0, state.x)
        assertEquals(4.0, state.y)
        view.y = 6.0
        assertEquals(5.0, state.x)
        assertEquals(6.0, state.y)

        view.set(7.0, 8.0)
        assertEquals(3, writes)
        val snapshot = Vector2.create(view)
        view.scale(2.0)
        assertEquals(4, writes)
        assertEquals(14.0, state.x)
        assertEquals(16.0, state.y)
        assertEquals(7.0, snapshot.x)
        assertEquals(8.0, snapshot.y)
    }

    @Test
    fun viewPropagatesOwnerLifetimeChecks() {
        var alive = true
        val view = LiveVector2(
            read = { check(alive); Vector2.create(1.0, 2.0) },
            write = { _, _ -> check(alive) },
        )
        alive = false
        assertFailsWith<IllegalStateException> { view.x }
        assertFailsWith<IllegalStateException> { view.set(1.0, 2.0) }
    }
}

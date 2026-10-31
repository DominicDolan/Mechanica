package com.mechanica.engine.physics

import com.cave.library.vector.vec2.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PhysicsWorldTest {
    @Test
    fun gravityMovesBodyAndWorldOwnsItsLifetime() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        val body = world.createDynamicCircle(Vector2.create(2.0, 10.0), 0.5)
        val initial = body.position
        repeat(60) { world.step(1.0 / 60.0) }
        val moved = body.position
        assertEquals(2.0, moved.x, 0.001)
        assertTrue(moved.y in 4.8..5.2, "Expected a body to fall roughly five meters, got ${moved.y}")
        assertEquals(10.0, initial.y)
        world.close()
        world.close()
        assertFailsWith<IllegalStateException> { body.position }
        assertFailsWith<IllegalStateException> { world.step(1.0 / 60.0) }
    }

    @Test
    fun invalidInputIsRejectedBeforeNativeCalls() {
        assertFailsWith<IllegalArgumentException> { PhysicsWorld(Vector2.create(Double.NaN, 0.0)) }
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        try {
            assertFailsWith<IllegalArgumentException> { world.step(0.0) }
            assertFailsWith<IllegalArgumentException> { world.step(1.0 / 60.0, 0) }
            assertFailsWith<IllegalArgumentException> { world.createDynamicCircle(Vector2.create(Double.POSITIVE_INFINITY, 0.0), 0.5) }
        } finally { world.close() }
    }
}

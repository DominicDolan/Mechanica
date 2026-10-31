package com.mechanica.engine.physics

import com.cave.library.vector.vec2.Vector2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FixedStepTest {
    @Test
    fun accumulatesAndCapsCatchUp() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        try {
            val stepper = FixedStep(world, stepSeconds = 0.125, maxStepsPerUpdate = 3)
            assertEquals(0, stepper.update(0.0625))
            assertEquals(0.5, stepper.interpolationAlpha, 0.00001)
            assertEquals(1, stepper.update(0.0625))
            assertEquals(0.0, stepper.interpolationAlpha, 0.00001)
            assertEquals(3, stepper.update(5.0))
            assertEquals(0, stepper.update(0.0))
            assertFailsWith<IllegalArgumentException> { stepper.update(-1.0) }
            assertFailsWith<IllegalArgumentException> { stepper.update(Double.NaN) }
        } finally { world.close() }
    }

    @Test
    fun invalidConfigurationAndClosedWorldFail() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        assertFailsWith<IllegalArgumentException> { FixedStep(world, stepSeconds = 0.0) }
        assertFailsWith<IllegalArgumentException> { FixedStep(world, maxStepsPerUpdate = 0) }
        val stepper = FixedStep(world)
        world.close()
        assertFailsWith<IllegalStateException> { stepper.update(0.0) }
    }
}

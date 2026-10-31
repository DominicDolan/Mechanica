package com.mechanica.engine.physics

import com.cave.library.vector.vec2.Vector2
import kotlin.test.Test
import kotlin.test.assertFailsWith

class WheelJointTest {
    @Test
    fun validatesOwnershipAndHandlesDestruction() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        val other = PhysicsWorld(Vector2.create(0.0, 0.0))
        try {
            val a = world.createDynamicCircle(Vector2.create(0.0, 2.0), 0.5)
            val b = world.createDynamicCircle(Vector2.create(1.0, 2.0), 0.5)
            val foreign = other.createDynamicCircle(Vector2.create(0.0, 0.0), 0.5)
            assertFailsWith<IllegalArgumentException> { world.buildWheelJoint(a, a) }
            assertFailsWith<IllegalArgumentException> { world.buildWheelJoint(a, foreign) }
            assertFailsWith<IllegalArgumentException> { world.buildWheelJoint(a, b) { axis.set(0.0, 0.0) } }
            assertFailsWith<IllegalArgumentException> { world.buildWheelJoint(a, b) { lowerTranslation = 2.0; upperTranslation = 1.0 } }
            val joint = world.buildWheelJoint(a, b)
            repeat(10) { world.step(1.0 / 120.0) }
            b.destroy()
            joint.destroy()
            joint.destroy()
            assertFailsWith<IllegalStateException> { world.buildWheelJoint(a, b) }
        } finally { world.close(); other.close() }
    }
}

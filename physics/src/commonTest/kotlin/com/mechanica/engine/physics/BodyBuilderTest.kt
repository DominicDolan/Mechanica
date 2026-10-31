package com.mechanica.engine.physics

import com.cave.library.angle.degrees
import com.cave.library.angle.radians
import com.cave.library.vector.vec2.Vector2
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BodyBuilderTest {
    @Test
    fun boxSettlesOnInclinedStaticSurface() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        try {
            val ground = world.buildBody {
                type = BodyType.Static
                angle = 0.2.radians
                buildShape { box(30.0, 0.5); friction = 1.0 }
            }
            val box = world.buildBody {
                type = BodyType.Dynamic
                position.set(0.0, 4.0)
                angle = 0.2.radians
                buildShape { box(1.0, 1.0); density = 2.0; friction = 1.0 }
            }
            val fixedStep = FixedStep(world)
            repeat(300) { fixedStep.update(1.0 / 60.0) }
            val position = box.position
            assertTrue(position.y in 0.5..1.1, "Expected box on slope, position=$position")
            assertTrue(abs(position.x) < 1.0, "Expected friction to hold box, position=$position")
            assertTrue(abs(box.linearVelocity.y) < 0.1)
            assertEquals(0.2, ground.angle.toRadians(), 0.003)
        } finally { world.close() }
    }

    @Test
    fun velocityIsLiveAndTransformIsExplicit() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        try {
            val body = world.buildBody {
                type = BodyType.Dynamic
                position.set(1.0, 4.0)
                buildShape { circle(0.5) }
                buildShape { center.set(0.5, 0.0); box(0.2, 0.2) }
            }
            val velocity = body.linearVelocity
            velocity.set(3.0, 4.0)
            velocity.x = 5.0
            assertEquals(5.0, velocity.x, 0.001)
            assertEquals(4.0, velocity.y, 0.001)
            val snapshot = Vector2.create(velocity)
            world.step(0.1)
            assertTrue(velocity.y < snapshot.y)
            assertEquals(4.0, snapshot.y, 0.001)
            velocity.scale(2.0)
            assertEquals(10.0, velocity.x, 0.001)
            body.setTransform(Vector2.create(10.0, 2.0), 0.5.radians)
            assertEquals(10.0, body.position.x, 0.001)
            assertEquals(2.0, body.position.y, 0.001)
            assertEquals(0.5, body.angle.toRadians(), 0.001)
            body.destroy()
            body.destroy()
            assertFailsWith<IllegalStateException> { velocity.x }
            assertFailsWith<IllegalStateException> { velocity.set(1.0, 2.0) }
            assertFailsWith<IllegalStateException> { body.position }
        } finally { world.close() }
    }

    @Test
    fun builderRejectsInvalidGeometryAndStaticVelocityWrites() {
        val world = PhysicsWorld(Vector2.create(0.0, -10.0))
        try {
            assertFailsWith<IllegalArgumentException> { world.buildBody { type = BodyType.Dynamic } }
            assertFailsWith<IllegalArgumentException> { world.buildBody { buildShape { box(-1.0, 2.0) } } }
            assertFailsWith<IllegalArgumentException> { world.buildBody { buildShape { circle(1.0); friction = -1.0 } } }
            assertFailsWith<IllegalArgumentException> { world.buildBody { type = BodyType.Dynamic; buildShape { circle(1.0); density = 0.0 } } }
            val floor = world.buildBody { buildShape { box(10.0, 1.0) } }
            assertFailsWith<IllegalStateException> { floor.linearVelocity.x = 1.0 }
            world.step(1.0 / 60.0)
        } finally { world.close() }
    }

    @Test
    fun degreesAndRadiansWorkAtThePhysicsBoundary() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        try {
            val body = world.buildBody {
                type = BodyType.Dynamic
                angle = 45.degrees
                buildShape { box(2.0, 0.5, angle = 30.degrees) }
            }
            assertEquals(45.0, body.angle.toDegrees(), 0.2)
            assertEquals(45.0, body.renderTransform(0.5).angle.toDegrees(), 0.2)
            body.setTransform(Vector2.create(1.0, 2.0), 90.degrees)
            assertEquals(90.0, body.angle.toDegrees(), 0.2)
            assertEquals(90.0, body.renderTransform(0.0).angle.toDegrees(), 0.2)
            assertFailsWith<IllegalArgumentException> { body.setTransform(Vector2.create(0.0, 0.0), Double.NaN.radians) }
            assertFailsWith<IllegalArgumentException> { world.buildBody { angle = Double.POSITIVE_INFINITY.degrees; buildShape { circle(1.0) } } }
            assertFailsWith<IllegalArgumentException> { world.buildBody { buildShape { box(1.0, 1.0, Double.NaN.radians) } } }
        } finally { world.close() }
    }

    @Test
    fun closingWorldInsideBuilderDoesNotCreateAStaleBody() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        assertFailsWith<IllegalStateException> {
            world.buildBody {
                buildShape { circle(1.0) }
                world.close()
            }
        }
    }
}

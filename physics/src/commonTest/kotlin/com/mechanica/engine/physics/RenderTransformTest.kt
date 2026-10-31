package com.mechanica.engine.physics

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.delta.Updater
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RenderTransformTest {
    @Test
    fun fixedGameClockAndWorldInterpolateTheLastPhysicsStep() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        try {
            val body = world.createDynamicCircle(Vector2.create(0.0, 0.0), 0.5)
            body.linearVelocity.set(2.0, 0.0)
            val calculator = DeltaCalculator.fixedStepCalculator(updatesPerSecond = 8.0, maxUpdatesPerFrame = 4)
            var renders = 0
            var updates = 0
            var drawnX = 0.0
            val updater = object : Updater {
                override fun update(delta: Double) { updates++; world.step(delta) }
                override fun render() { renders++; drawnX = body.renderTransform(calculator.interpolationAlpha).position.x }
            }

            calculator.updateAndRender(0.0, 0.0625, updater)
            assertEquals(0, updates)
            assertEquals(0.0, drawnX, 0.0001)
            calculator.updateAndRender(0.0625, 0.1875, updater)
            assertEquals(1, updates)
            assertEquals(0.5, calculator.interpolationAlpha, 0.0001)
            assertEquals(0.125, drawnX, 0.001)
            calculator.updateAndRender(0.1875, 0.4375, updater)
            assertEquals(3, updates)
            assertEquals(3, renders)
            assertEquals(0.625, drawnX, 0.001)
            assertEquals(0.75, body.position.x, 0.001)

            calculator.step(updater, calculator.timeStep)
            assertEquals(1.0, calculator.interpolationAlpha)
            assertEquals(body.position.x, drawnX, 0.001)
            calculator.updateAndRender(0.0, 0.0625, updater)
            assertEquals(1.0, calculator.interpolationAlpha)
            assertEquals(body.position.x, drawnX, 0.001)
            calculator.resync()
            assertEquals(1.0, calculator.interpolationAlpha)
        } finally { world.close() }
    }

    @Test
    fun snapshotsResetOnTeleportAndBodiesHaveIndependentLifetimes() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        try {
            val moving = world.createDynamicCircle(Vector2.create(2.0, 0.0), 0.5)
            val fixed = world.buildBody { position.set(1.0, 2.0); buildShape { box(1.0, 1.0) } }
            moving.linearVelocity.x = 4.0
            world.step(0.125)
            assertEquals(2.25, moving.renderTransform(0.5).position.x, 0.001)
            val newBody = world.createDynamicCircle(Vector2.create(5.0, 6.0), 0.5)
            assertEquals(5.0, newBody.renderTransform(0.0).position.x, 0.001)
            moving.setTransform(Vector2.create(20.0, 3.0), 0.5)
            assertEquals(20.0, moving.renderTransform(0.0).position.x, 0.001)
            assertEquals(20.0, moving.renderTransform(0.5).position.x, 0.001)
            fixed.setTransform(Vector2.create(-4.0, 0.0), 0.0)
            assertEquals(-4.0, fixed.renderTransform(0.5).position.x, 0.001)
            assertFailsWith<IllegalArgumentException> { moving.renderTransform(Double.NaN) }
            assertFailsWith<IllegalArgumentException> { moving.renderTransform(1.1) }
            moving.destroy()
            assertFailsWith<IllegalStateException> { moving.renderTransform(0.5) }
            world.step(0.125)
            assertEquals(5.0, newBody.renderTransform(1.0).position.x, 0.001)
            world.close()
            assertFailsWith<IllegalStateException> { newBody.renderTransform(0.5) }
        } finally { world.close() }
    }

    @Test
    fun rotationInterpolatesAcrossPiByTheShortPath() {
        val world = PhysicsWorld(Vector2.create(0.0, 0.0))
        try {
            val body = world.buildBody {
                type = BodyType.Dynamic
                angle = 3.1
                buildShape { circle(0.5) }
            }
            body.angularVelocity = 1.0
            world.step(0.125)
            val before = body.renderTransform(0.0).angle
            val after = body.renderTransform(1.0).angle
            val middle = body.renderTransform(0.5).angle
            assertTrue(abs(after - before) < 0.3, "Rotation should take the short path: $before to $after")
            assertTrue(middle > 3.0 && middle < PI + 0.2, "Unexpected middle angle $middle")
        } finally { world.close() }
    }
}

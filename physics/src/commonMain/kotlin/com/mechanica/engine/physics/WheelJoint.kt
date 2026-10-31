package com.mechanica.engine.physics

import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.physics.internal.PlatformPhysics
import kotlin.math.hypot

/** The anchor and suspension axis are in world space at joint creation. */
@PhysicsDsl
class WheelJointBuilder internal constructor(initialAnchor: Vector2) {
    val anchor: MutableVector2 = MutableVector2.create(initialAnchor)
    val axis: MutableVector2 = MutableVector2.create(0.0, 1.0)
    var springHertz: Double = 4.0
    var dampingRatio: Double = 0.7
    var lowerTranslation: Double = -0.25
    var upperTranslation: Double = 0.25

    internal fun validated(): WheelDefinition {
        val length = hypot(axis.x, axis.y)
        require(length.isFinite() && length > 0.0) { "Wheel axis must be finite and nonzero" }
        val hz = springHertz.toPhysicsFloat()
        val damping = dampingRatio.toPhysicsFloat()
        val lower = lowerTranslation.toPhysicsFloat()
        val upper = upperTranslation.toPhysicsFloat()
        require(hz > 0f && damping >= 0f) { "Spring frequency must be positive and damping non-negative" }
        require(lower <= upper) { "Lower translation must not exceed upper translation" }
        return WheelDefinition(anchor.x.toPhysicsFloat(), anchor.y.toPhysicsFloat(), (axis.x / length).toPhysicsFloat(),
            (axis.y / length).toPhysicsFloat(), hz, damping, lower, upper)
    }
}

internal data class WheelDefinition(val ax: Float, val ay: Float, val ux: Float, val uy: Float,
    val hertz: Float, val damping: Float, val lower: Float, val upper: Float)

/** Owned by the world; destroying either connected body also destroys the joint. */
class WheelJoint internal constructor(private val world: PhysicsWorld, private val a: PhysicsBody,
    private val b: PhysicsBody, private val handle: Long) {
    private var destroyed = false
    fun destroy() {
        if (!destroyed) {
            if (!world.isClosed && a.isAlive && b.isAlive) PlatformPhysics.destroyWheel(handle)
            destroyed = true
        }
    }
}

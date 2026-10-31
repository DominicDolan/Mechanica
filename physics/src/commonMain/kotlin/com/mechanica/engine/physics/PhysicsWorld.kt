package com.mechanica.engine.physics

import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.physics.internal.LiveVector2
import com.mechanica.engine.physics.internal.PlatformPhysics
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** An explicitly owned Box2D world. Confine all access to its simulation thread. */
class PhysicsWorld(gravity: Vector2) {
    private val handle: Int = PlatformPhysics.createWorld(gravity.x.toPhysicsFloat(), gravity.y.toPhysicsFloat()).also {
        check(it != 0) { "Box2D could not create a physics world" }
    }
    private var closed = false
    private val movingBodies = mutableSetOf<PhysicsBody>()

    /** Validates all definitions before creating anything in the simulation. */
    fun buildBody(block: BodyBuilder.() -> Unit): PhysicsBody {
        checkOpen()
        val definition = BodyBuilder().apply(block).validated()
        checkOpen()
        val body = PlatformPhysics.createBody(handle, definition.type == BodyType.Dynamic, definition.x, definition.y, definition.angle)
        check(body != 0L) { "Box2D could not create a body" }
        try {
            for (shape in definition.shapes) {
                when (val geometry = shape.geometry) {
                    is Geometry.Circle -> PlatformPhysics.addCircle(body, shape.x, shape.y, geometry.radius, shape.density, shape.friction, shape.restitution)
                    is Geometry.Box -> PlatformPhysics.addBox(body, shape.x, shape.y, geometry.halfWidth, geometry.halfHeight, geometry.angle, shape.density, shape.friction, shape.restitution)
                }
            }
            return PhysicsBody(this, body, definition.type).also {
                if (definition.type == BodyType.Dynamic) movingBodies.add(it)
            }
        } catch (error: Throwable) {
            PlatformPhysics.destroyBody(body)
            throw error
        }
    }

    /** Convenience for a single dynamic circle. */
    fun createDynamicCircle(position: Vector2, radius: Double): PhysicsBody {
        val initialPosition = position
        return buildBody {
            type = BodyType.Dynamic
            this.position.set(initialPosition)
            buildShape { circle(radius) }
        }
    }

    fun step(deltaSeconds: Double, substeps: Int = 4) {
        checkOpen()
        require(deltaSeconds > 0.0 && deltaSeconds.isFinite()) { "Time step must be finite and positive" }
        require(substeps > 0) { "Substeps must be positive" }
        val delta = deltaSeconds.toPhysicsFloat()
        require(delta > 0f) { "Time step is too small for Box2D" }
        movingBodies.forEach { it.capturePreviousTransform() }
        PlatformPhysics.stepWorld(handle, delta, substeps)
        movingBodies.forEach { it.captureCurrentTransform() }
    }

    /** Destroys the world and its bodies; safe to call more than once. */
    fun close() {
        if (!closed) {
            PlatformPhysics.destroyWorld(handle)
            closed = true
            movingBodies.clear()
        }
    }

    internal fun checkOpen() = check(!closed) { "Physics world is closed" }
    internal val isClosed: Boolean get() = closed
    internal fun forget(body: PhysicsBody) { movingBodies.remove(body) }
}

enum class BodyType { Static, Dynamic }

/** An independent world-space position and angle, in radians. */
data class PhysicsTransform(val position: Vector2, val angle: Double)

class PhysicsBody internal constructor(private val world: PhysicsWorld, private val handle: Long, val type: BodyType) {
    private var destroyed = false
    private var currentTransform = readTransform()
    private var previousTransform = currentTransform

    private fun readTransform() = PhysicsTransform(
        Vector2.create(PlatformPhysics.positionX(handle).toDouble(), PlatformPhysics.positionY(handle).toDouble()),
        PlatformPhysics.angle(handle).toDouble(),
    )

    internal fun capturePreviousTransform() { previousTransform = currentTransform }
    internal fun captureCurrentTransform() { currentTransform = readTransform() }
    private fun checkAlive() {
        world.checkOpen()
        check(!destroyed) { "Physics body is destroyed" }
    }

    /** A snapshot of the body origin, in world units. */
    val position: Vector2
        get() {
            checkAlive()
            return Vector2.create(PlatformPhysics.positionX(handle).toDouble(), PlatformPhysics.positionY(handle).toDouble())
        }

    /** Rotation in radians. */
    val angle: Double get() { checkAlive(); return PlatformPhysics.angle(handle).toDouble() }

    /** Interpolates the two most recent physics steps along the shortest rotation. */
    fun renderTransform(alpha: Double): PhysicsTransform {
        checkAlive()
        require(alpha.isFinite() && alpha in 0.0..1.0) { "Interpolation alpha must be between zero and one" }
        val before = previousTransform
        val after = currentTransform
        val deltaAngle = after.angle - before.angle
        val shortestAngle = atan2(sin(deltaAngle), cos(deltaAngle))
        return PhysicsTransform(
            Vector2.create(
                before.position.x + (after.position.x - before.position.x) * alpha,
                before.position.y + (after.position.y - before.position.y) * alpha,
            ),
            before.angle + shortestAngle * alpha,
        )
    }

    /** Angular velocity in radians per second. */
    var angularVelocity: Double
        get() { checkAlive(); return PlatformPhysics.angularVelocity(handle).toDouble() }
        set(value) {
            checkAlive()
            check(type == BodyType.Dynamic) { "Static bodies cannot have velocity" }
            PlatformPhysics.setAngularVelocity(handle, value.toPhysicsFloat())
        }

    /** A live view in world units per second; modifying a static body is an error. */
    val linearVelocity: MutableVector2 = LiveVector2(
        read = {
            checkAlive()
            Vector2.create(PlatformPhysics.velocityX(handle).toDouble(), PlatformPhysics.velocityY(handle).toDouble())
        },
        write = { x, y ->
            checkAlive()
            check(type == BodyType.Dynamic) { "Static bodies cannot have velocity" }
            PlatformPhysics.setVelocity(handle, x.toPhysicsFloat(), y.toPhysicsFloat())
        },
    )

    /** Teleports the body; use velocity or forces for normal movement. */
    fun setTransform(position: Vector2, angle: Double) {
        checkAlive()
        PlatformPhysics.setTransform(handle, position.x.toPhysicsFloat(), position.y.toPhysicsFloat(), angle.toPhysicsFloat())
        currentTransform = readTransform()
        previousTransform = currentTransform
    }

    /** Destroys this body and its shapes. Safe after a previous destroy or world close. */
    fun destroy() {
        if (!destroyed) {
            if (!world.isClosed) {
                world.forget(this)
                PlatformPhysics.destroyBody(handle)
            }
            destroyed = true
        }
    }
}

internal fun Double.toPhysicsFloat(): Float {
    require(isFinite()) { "Physics values must be finite" }
    return toFloat().also { require(it.isFinite()) { "Physics value is outside Float range" } }
}

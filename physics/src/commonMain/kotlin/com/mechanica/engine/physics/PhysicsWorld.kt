package com.mechanica.engine.physics

import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.cave.library.angle.Angle
import com.cave.library.angle.radians
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

    /** Static open chain. The collidable side is to the RIGHT of each directed edge.
     * Explicit ghost neighbours preserve continuity between streamed pieces. They are
     * not collidable edges. At open ends they default to linear extrapolation.
     */
    fun createTerrainChain(points: List<Vector2>, friction: Double = 0.6,
                           previousGhost: Vector2? = null, nextGhost: Vector2? = null): PhysicsBody {
        checkOpen()
        require(points.size >= 2) { "A terrain chain needs at least two points" }
        val f = friction.toPhysicsFloat()
        require(f >= 0f)
        val xy = FloatArray((points.size + 2) * 2)
        points.forEachIndexed { index, p ->
            xy[(index + 1) * 2] = p.x.toPhysicsFloat()
            xy[(index + 1) * 2 + 1] = p.y.toPhysicsFloat()
        }
        for (i in 1 until points.size) {
            val dx = xy[(i + 1) * 2] - xy[i * 2]
            val dy = xy[(i + 1) * 2 + 1] - xy[i * 2 + 1]
            require(dx * dx + dy * dy >= 0.0001f) { "Terrain points must be at least 1cm apart" }
        }
        xy[0] = previousGhost?.x?.toPhysicsFloat() ?: (2.0 * xy[2] - xy[4]).toPhysicsFloat()
        xy[1] = previousGhost?.y?.toPhysicsFloat() ?: (2.0 * xy[3] - xy[5]).toPhysicsFloat()
        val end = xy.size - 2
        xy[end] = nextGhost?.x?.toPhysicsFloat() ?: (2.0 * xy[end - 2] - xy[end - 4]).toPhysicsFloat()
        xy[end + 1] = nextGhost?.y?.toPhysicsFloat() ?: (2.0 * xy[end - 1] - xy[end - 3]).toPhysicsFloat()
        for (i in listOf(0, end - 2)) {
            val dx = xy[i + 2] - xy[i]; val dy = xy[i + 3] - xy[i + 1]
            require(dx * dx + dy * dy >= 0.0001f) { "Ghost neighbours must be at least 1cm from chain endpoints" }
        }
        val body = PlatformPhysics.createBody(handle, false, 0f, 0f, 0f)
        check(body != 0L)
        try {
            check(PlatformPhysics.addChain(body, xy, f)) { "Could not create terrain chain" }
            return PhysicsBody(this, body, BodyType.Static)
        } catch (error: Throwable) { PlatformPhysics.destroyBody(body); throw error }
    }

    /** Downward ray against static walkable surfaces; returns distance, or null on a miss. */
    fun groundDistance(position: Vector2, length: Double): Double? {
        checkOpen()
        val distance = length.toPhysicsFloat()
        require(distance > 0f)
        val fraction = PlatformPhysics.groundRay(handle, position.x.toPhysicsFloat(), position.y.toPhysicsFloat(), distance)
        return if (fraction < 0f) null else fraction.toDouble() * length
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

    /** Connect two bodies with a suspended, freely rotating wheel at a world-space anchor. */
    fun buildWheelJoint(chassis: PhysicsBody, wheel: PhysicsBody, block: WheelJointBuilder.() -> Unit = {}): WheelJoint {
        checkOpen()
        require(chassis !== wheel) { "A joint needs two distinct bodies" }
        chassis.requireIn(this)
        wheel.requireIn(this)
        require(chassis.type == BodyType.Dynamic || wheel.type == BodyType.Dynamic) { "At least one joint body must be dynamic" }
        val definition = WheelJointBuilder(wheel.position).apply(block).validated()
        checkOpen()
        chassis.requireIn(this)
        wheel.requireIn(this)
        val joint = PlatformPhysics.createWheel(handle, chassis.handle, wheel.handle, definition.ax, definition.ay,
            definition.ux, definition.uy, definition.hertz, definition.damping, definition.lower, definition.upper)
        check(joint != 0L) { "Box2D could not create a wheel joint" }
        return WheelJoint(this, chassis, wheel, joint)
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

/** An independent world-space position and angle. */
data class PhysicsTransform(val position: Vector2, val angle: Angle)

class PhysicsBody internal constructor(private val world: PhysicsWorld, internal val handle: Long, val type: BodyType) {
    private var destroyed = false
    private var currentTransform = readTransform()
    private var previousTransform = currentTransform

    private fun readTransform() = PhysicsTransform(
        Vector2.create(PlatformPhysics.positionX(handle).toDouble(), PlatformPhysics.positionY(handle).toDouble()),
        PlatformPhysics.angle(handle).toDouble().radians,
    )

    internal fun capturePreviousTransform() { previousTransform = currentTransform }
    internal fun captureCurrentTransform() { currentTransform = readTransform() }
    private fun checkAlive() {
        world.checkOpen()
        check(!destroyed) { "Physics body is destroyed" }
    }
    internal val isAlive: Boolean get() = !world.isClosed && !destroyed
    internal fun requireIn(owner: PhysicsWorld) {
        require(world === owner) { "Joint bodies must belong to this world" }
        checkAlive()
    }

    /** A snapshot of the body origin, in world units. */
    val position: Vector2
        get() {
            checkAlive()
            return Vector2.create(PlatformPhysics.positionX(handle).toDouble(), PlatformPhysics.positionY(handle).toDouble())
        }

    /** World-space rotation. */
    val angle: Angle get() { checkAlive(); return PlatformPhysics.angle(handle).toDouble().radians }

    /** Interpolates the two most recent physics steps along the shortest rotation. */
    fun renderTransform(alpha: Double): PhysicsTransform {
        checkAlive()
        require(alpha.isFinite() && alpha in 0.0..1.0) { "Interpolation alpha must be between zero and one" }
        val before = previousTransform
        val after = currentTransform
        val deltaAngle = after.angle.toRadians() - before.angle.toRadians()
        val shortestAngle = atan2(sin(deltaAngle), cos(deltaAngle))
        return PhysicsTransform(
            Vector2.create(
                before.position.x + (after.position.x - before.position.x) * alpha,
                before.position.y + (after.position.y - before.position.y) * alpha,
            ),
            (before.angle.toRadians() + shortestAngle * alpha).radians,
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

    /** Lock a character upright while retaining normal collision response. */
    fun setFixedRotation(fixed: Boolean) { checkAlive(); PlatformPhysics.fixedRotation(handle, fixed) }

    /** Teleports the body; use velocity or forces for normal movement. */
    fun setTransform(position: Vector2, angle: Angle) {
        checkAlive()
        PlatformPhysics.setTransform(handle, position.x.toPhysicsFloat(), position.y.toPhysicsFloat(), angle.toRadians().toPhysicsFloat())
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

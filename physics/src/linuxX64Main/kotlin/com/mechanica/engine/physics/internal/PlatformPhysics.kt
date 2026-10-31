@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package com.mechanica.engine.physics.internal

import com.mechanica.engine.physics.binding.*
import kotlinx.cinterop.*

internal actual object PlatformPhysics {
    actual fun createWorld(x: Float, y: Float) = mp_world_create(x, y).toInt()
    actual fun destroyWorld(world: Int) = mp_world_destroy(world.toUInt())
    actual fun stepWorld(world: Int, delta: Float, substeps: Int) = mp_world_step(world.toUInt(), delta, substeps)
    actual fun createBody(world: Int, dynamic: Boolean, x: Float, y: Float, angle: Float) = mp_body_create(world.toUInt(), if (dynamic) 1 else 0, x, y, angle).toLong()
    actual fun createWheel(world: Int, a: Long, b: Long, ax: Float, ay: Float, ux: Float, uy: Float, hertz: Float, damping: Float, lower: Float, upper: Float) = mp_wheel_create(world.toUInt(), a.toULong(), b.toULong(), ax, ay, ux, uy, hertz, damping, lower, upper).toLong()
    actual fun destroyWheel(joint: Long) = mp_wheel_destroy(joint.toULong())
    actual fun addChain(body: Long, xy: FloatArray, friction: Float) = xy.usePinned { mp_body_add_chain(body.toULong(), it.addressOf(0), xy.size / 2, friction) != 0 }
    actual fun fixedRotation(body: Long, fixed: Boolean) = mp_body_fixed_rotation(body.toULong(), if (fixed) 1 else 0)
    actual fun groundRay(world: Int, x: Float, y: Float, length: Float) = mp_world_ground_ray(world.toUInt(), x, y, length)
    actual fun destroyBody(body: Long) = mp_body_destroy(body.toULong())
    actual fun addCircle(body: Long, x: Float, y: Float, radius: Float, density: Float, friction: Float, restitution: Float) = mp_body_add_circle(body.toULong(), x, y, radius, density, friction, restitution)
    actual fun addBox(body: Long, x: Float, y: Float, halfWidth: Float, halfHeight: Float, angle: Float, density: Float, friction: Float, restitution: Float) = mp_body_add_box(body.toULong(), x, y, halfWidth, halfHeight, angle, density, friction, restitution)
    actual fun positionX(body: Long) = mp_body_position_x(body.toULong())
    actual fun positionY(body: Long) = mp_body_position_y(body.toULong())
    actual fun angle(body: Long) = mp_body_angle(body.toULong())
    actual fun setTransform(body: Long, x: Float, y: Float, angle: Float) = mp_body_set_transform(body.toULong(), x, y, angle)
    actual fun velocityX(body: Long) = mp_body_velocity_x(body.toULong())
    actual fun velocityY(body: Long) = mp_body_velocity_y(body.toULong())
    actual fun angularVelocity(body: Long) = mp_body_angular_velocity(body.toULong())
    actual fun setAngularVelocity(body: Long, velocity: Float) = mp_body_set_angular_velocity(body.toULong(), velocity)
    actual fun setVelocity(body: Long, x: Float, y: Float) = mp_body_set_velocity(body.toULong(), x, y)
}

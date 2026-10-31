package com.mechanica.engine.physics.internal

import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal object NativeBridge {
    init {
        check(System.getProperty("os.name").lowercase().contains("linux") && System.getProperty("os.arch") in setOf("amd64", "x86_64")) {
            "Mechanica physics currently supports JVM on Linux x64 only"
        }
        val resource = "/com/mechanica/engine/physics/linux-x64/libmechanica_physics_jni.so"
        val stream = NativeBridge::class.java.getResourceAsStream(resource)
            ?: error("Physics native library not found: $resource")
        val library = Files.createTempFile("mechanica-physics-", ".so")
        stream.use { Files.copy(it, library, StandardCopyOption.REPLACE_EXISTING) }
        library.toFile().deleteOnExit()
        System.load(library.toAbsolutePath().toString())
    }
    external fun worldCreate(x: Float, y: Float): Int
    external fun worldDestroy(world: Int)
    external fun worldStep(world: Int, delta: Float, substeps: Int)
    external fun bodyCreate(world: Int, dynamic: Boolean, x: Float, y: Float, angle: Float): Long
    external fun wheelCreate(world: Int, a: Long, b: Long, ax: Float, ay: Float, ux: Float, uy: Float, hertz: Float, damping: Float, lower: Float, upper: Float): Long
    external fun wheelDestroy(joint: Long)
    external fun bodyAddChain(body: Long, xy: FloatArray, friction: Float): Boolean
    external fun bodyFixedRotation(body: Long, fixed: Boolean)
    external fun worldGroundRay(world: Int, x: Float, y: Float, length: Float): Float
    external fun bodyDestroy(body: Long)
    external fun bodyAddCircle(body: Long, x: Float, y: Float, radius: Float, density: Float, friction: Float, restitution: Float)
    external fun bodyAddBox(body: Long, x: Float, y: Float, halfWidth: Float, halfHeight: Float, angle: Float, density: Float, friction: Float, restitution: Float)
    external fun bodyPositionX(body: Long): Float
    external fun bodyPositionY(body: Long): Float
    external fun bodyAngle(body: Long): Float
    external fun bodySetTransform(body: Long, x: Float, y: Float, angle: Float)
    external fun bodyVelocityX(body: Long): Float
    external fun bodyVelocityY(body: Long): Float
    external fun bodyAngularVelocity(body: Long): Float
    external fun bodySetAngularVelocity(body: Long, velocity: Float)
    external fun bodySetVelocity(body: Long, x: Float, y: Float)
}

internal actual object PlatformPhysics {
    actual fun createWorld(x: Float, y: Float) = NativeBridge.worldCreate(x, y)
    actual fun destroyWorld(world: Int) = NativeBridge.worldDestroy(world)
    actual fun stepWorld(world: Int, delta: Float, substeps: Int) = NativeBridge.worldStep(world, delta, substeps)
    actual fun createBody(world: Int, dynamic: Boolean, x: Float, y: Float, angle: Float) = NativeBridge.bodyCreate(world, dynamic, x, y, angle)
    actual fun createWheel(world: Int, a: Long, b: Long, ax: Float, ay: Float, ux: Float, uy: Float, hertz: Float, damping: Float, lower: Float, upper: Float) = NativeBridge.wheelCreate(world, a, b, ax, ay, ux, uy, hertz, damping, lower, upper)
    actual fun destroyWheel(joint: Long) = NativeBridge.wheelDestroy(joint)
    actual fun addChain(body: Long, xy: FloatArray, friction: Float) = NativeBridge.bodyAddChain(body, xy, friction)
    actual fun fixedRotation(body: Long, fixed: Boolean) = NativeBridge.bodyFixedRotation(body, fixed)
    actual fun groundRay(world: Int, x: Float, y: Float, length: Float) = NativeBridge.worldGroundRay(world, x, y, length)
    actual fun destroyBody(body: Long) = NativeBridge.bodyDestroy(body)
    actual fun addCircle(body: Long, x: Float, y: Float, radius: Float, density: Float, friction: Float, restitution: Float) = NativeBridge.bodyAddCircle(body, x, y, radius, density, friction, restitution)
    actual fun addBox(body: Long, x: Float, y: Float, halfWidth: Float, halfHeight: Float, angle: Float, density: Float, friction: Float, restitution: Float) = NativeBridge.bodyAddBox(body, x, y, halfWidth, halfHeight, angle, density, friction, restitution)
    actual fun positionX(body: Long) = NativeBridge.bodyPositionX(body)
    actual fun positionY(body: Long) = NativeBridge.bodyPositionY(body)
    actual fun angle(body: Long) = NativeBridge.bodyAngle(body)
    actual fun setTransform(body: Long, x: Float, y: Float, angle: Float) = NativeBridge.bodySetTransform(body, x, y, angle)
    actual fun velocityX(body: Long) = NativeBridge.bodyVelocityX(body)
    actual fun velocityY(body: Long) = NativeBridge.bodyVelocityY(body)
    actual fun angularVelocity(body: Long) = NativeBridge.bodyAngularVelocity(body)
    actual fun setAngularVelocity(body: Long, velocity: Float) = NativeBridge.bodySetAngularVelocity(body, velocity)
    actual fun setVelocity(body: Long, x: Float, y: Float) = NativeBridge.bodySetVelocity(body, x, y)
}

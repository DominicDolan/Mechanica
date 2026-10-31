package com.mechanica.engine.physics.internal

internal expect object PlatformPhysics {
    fun createWorld(x: Float, y: Float): Int
    fun destroyWorld(world: Int)
    fun stepWorld(world: Int, delta: Float, substeps: Int)
    fun createBody(world: Int, dynamic: Boolean, x: Float, y: Float, angle: Float): Long
    fun destroyBody(body: Long)
    fun addCircle(body: Long, x: Float, y: Float, radius: Float, density: Float, friction: Float, restitution: Float)
    fun addBox(body: Long, x: Float, y: Float, halfWidth: Float, halfHeight: Float, angle: Float, density: Float, friction: Float, restitution: Float)
    fun positionX(body: Long): Float
    fun positionY(body: Long): Float
    fun angle(body: Long): Float
    fun setTransform(body: Long, x: Float, y: Float, angle: Float)
    fun velocityX(body: Long): Float
    fun velocityY(body: Long): Float
    fun angularVelocity(body: Long): Float
    fun setAngularVelocity(body: Long, velocity: Float)
    fun setVelocity(body: Long, x: Float, y: Float)
}

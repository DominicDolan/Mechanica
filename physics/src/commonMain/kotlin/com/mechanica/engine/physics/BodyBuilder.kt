package com.mechanica.engine.physics

import com.cave.library.vector.vec2.MutableVector2

@DslMarker
annotation class PhysicsDsl

/** Body transforms use world coordinates and radians. */
@PhysicsDsl
class BodyBuilder internal constructor() {
    var type: BodyType = BodyType.Static
    val position: MutableVector2 = MutableVector2.create()
    var angle: Double = 0.0
    private val shapes = mutableListOf<ShapeBuilder>()

    fun buildShape(block: ShapeBuilder.() -> Unit) {
        shapes.add(ShapeBuilder().apply(block))
    }

    internal fun validated(): BodyDefinition {
        val builtShapes = shapes.map { it.validated() }
        require(builtShapes.isNotEmpty()) { "A body requires at least one shape" }
        require(type != BodyType.Dynamic || builtShapes.any { it.density > 0f }) { "A dynamic body needs a shape with positive density" }
        return BodyDefinition(type, position.x.toPhysicsFloat(), position.y.toPhysicsFloat(), angle.toPhysicsFloat(), builtShapes)
    }
}

/** Shape centers and box angles are relative to the body. Dimensions are full widths/heights. */
@PhysicsDsl
class ShapeBuilder internal constructor() {
    val center: MutableVector2 = MutableVector2.create()
    var density: Double = 1.0
    var friction: Double = 0.6
    var restitution: Double = 0.0
    private var geometry: Geometry? = null

    fun circle(radius: Double) {
        val r = radius.toPhysicsFloat()
        require(r > 0f) { "Circle radius must be positive" }
        setGeometry(Geometry.Circle(r))
    }

    fun box(width: Double, height: Double, angle: Double = 0.0) {
        val halfWidth = (width / 2.0).toPhysicsFloat()
        val halfHeight = (height / 2.0).toPhysicsFloat()
        require(halfWidth > 0f && halfHeight > 0f) { "Box dimensions must be positive" }
        setGeometry(Geometry.Box(halfWidth, halfHeight, angle.toPhysicsFloat()))
    }

    private fun setGeometry(value: Geometry) {
        check(geometry == null) { "A shape must have exactly one geometry" }
        geometry = value
    }

    internal fun validated(): ShapeDefinition {
        val d = density.toPhysicsFloat()
        val f = friction.toPhysicsFloat()
        val r = restitution.toPhysicsFloat()
        require(d >= 0f && f >= 0f && r >= 0f) { "Density, friction and restitution must be non-negative" }
        return ShapeDefinition(requireNotNull(geometry) { "Shape geometry is required" }, center.x.toPhysicsFloat(), center.y.toPhysicsFloat(), d, f, r)
    }
}

internal sealed interface Geometry {
    data class Circle(val radius: Float) : Geometry
    data class Box(val halfWidth: Float, val halfHeight: Float, val angle: Float) : Geometry
}
internal data class ShapeDefinition(val geometry: Geometry, val x: Float, val y: Float, val density: Float, val friction: Float, val restitution: Float)
internal data class BodyDefinition(val type: BodyType, val x: Float, val y: Float, val angle: Float, val shapes: List<ShapeDefinition>)

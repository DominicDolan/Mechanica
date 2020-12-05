package com.mechanica.engine.unit.vector

object VectorShapes {

    fun createQuad(left: Number, top: Number, right: Number, bottom: Number): Array<Vector> {
        return arrayOf(
                vec(left, top),
                vec(left, bottom),
                vec(right, bottom),
                vec(left, top),
                vec(right, bottom),
                vec(right, top))

    }

    fun createRectangle(x: Double, y: Double, width: Double, height: Double): Array<Vector> {
        return createQuad(x, y + height, x + width, y)
    }

    fun createUnitSquare(): Array<Vector> {
        return createQuad(0f, 1f, 1f, 0f)
    }

    fun createInvertedUnitSquare(): Array<Vector> {
        return createQuad(0f, 0f, 1f, 1f)
    }

}
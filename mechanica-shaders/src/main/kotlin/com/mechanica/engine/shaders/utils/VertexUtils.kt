package com.mechanica.engine.shaders.utils

import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec


fun createUnitSquareFloats() = createQuadFloatArray(0f, 1f, 1f, 0f)

fun createInvertedUnitSquareFloats() = createTextureQuadFloatArray(0f, 1f, 1f, 0f)

/**
 *  Creates an array of shorts of size numberOfQuads*6 to be used with a series of quadrilaterals
 *
 *  For the first quad, the array will have indices in the form of:
 *
 *      0, 1, 2,
 *      2, 1, 3
 *  For the second set it will be:
 *
 *      4, 5, 6,
 *      6, 5, 7
 *  i.e. the first set + 4
 *  and so on
 *
 *  @param numberOfQuads the number of quadrilaterals to be represented by the index array
 *  @return The short array filled with the indices data for the required number of quads
 *
 *
 */
fun createIndicesArrayForQuads(numberOfQuads: Int): ShortArray {
    val array = ShortArray(numberOfQuads*6)
    val first = shortArrayOf(
            0, 1, 2, //First triangle
            2, 1, 3  //Second Triangle
    )
    for (i in 0 until numberOfQuads) {
        for (j in first.indices) {
            array[i*6 + j] = (first[j] + i*4).toShort()
        }
    }
    return array
}

fun createQuadVecArray(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, top),
            vec(left, bottom),
            vec(right, bottom),
            vec(left, top),
            vec(right, bottom),
            vec(right, top))

}

fun createQuadFloatArray(left: Float, top: Float, right: Float, bottom: Float): FloatArray {
    return floatArrayOf(
            left, top, 0f,
            left, bottom, 0f,
            right, bottom, 0f,
            left, top, 0f,
            right, bottom, 0f,
            right, top, 0f)

}

fun createUnitSquareVectors(): Array<Vector> {
    return createQuadVecArray(0f, 1f, 1f, 0f)
}

fun createInvertedUnitSquareVectors(): Array<Vector> {
    return createQuadVecArray(0f, 0f, 1f, 1f)
}

/*

val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

vertices = floatArrayOf(
    0f, 1f, //V0 top left
    0f, 0f, //V1 bottom left
    1f, 0f, //V2 bottom right
    1f, 1f  //V3 top right
)

textureCoords = floatArrayOf(
        0f, 0f, //V0
        0f, 1f, //V1
        1f, 1f, //V2
        1f, 0f  //V3
)

*/

private fun createTextureQuadVecArray(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, bottom),
            vec(left, top),
            vec(right, top),
            vec(left, bottom),
            vec(right, top),
            vec(right, bottom))

}

private fun createTextureQuadFloatArray(left: Float, top: Float, right: Float, bottom: Float): FloatArray {
    return floatArrayOf(
            left, bottom,
            left, top,
            right, top,
            left, bottom,
            right, top,
            right, bottom)

}

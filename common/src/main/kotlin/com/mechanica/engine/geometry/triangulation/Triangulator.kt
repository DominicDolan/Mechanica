package com.mechanica.engine.geometry.triangulation


interface Triangulator {
    val indices: ShortArray
    val indexCount: Int

    fun triangulate(): ShortArray

}
package com.mechanica.engine.geometry.triangulation


interface Triangulator {
    val indexCount: Int

    fun triangulate(): ShortArray

}
package com.mechanica.engine.display

interface Display {
    val id: Long
    val name: String
    val physicalSize: Size
    val contentScale: ContentScale

    val width: Int
    val height: Int

    data class Size(val width_mm: Int, val height_mm: Int)
    data class ContentScale(val xScale: Float, val yScale: Float)

}
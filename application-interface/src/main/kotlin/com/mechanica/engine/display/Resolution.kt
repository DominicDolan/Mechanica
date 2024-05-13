package com.mechanica.engine.display

interface Resolution {
    val width: Pixel
    val height: Pixel

    val aspectRatio: Double
        get() = if (width.toInt() != 0 && height.toInt() != 0) width.toDouble()/height.toDouble() else 1.0
}


@JvmInline
value class Pixel(private val value: Int) {
    fun toInt() = value
    fun toDouble() = value.toDouble()
}

val Int.px: Pixel get() = Pixel(this)
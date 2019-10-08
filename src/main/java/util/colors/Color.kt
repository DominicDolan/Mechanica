package util.colors

interface Color {
    val a: Double
    val r: Double
    val g: Double
    val b: Double

    fun toLong(): Long
}
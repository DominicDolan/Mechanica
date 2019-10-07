package util.colors

interface Color {
    fun toHex(): HexColor
    fun toRgba(): RGBAColor
    fun lighten(amount: Int): Color
    fun darken(amount: Int): Color
}
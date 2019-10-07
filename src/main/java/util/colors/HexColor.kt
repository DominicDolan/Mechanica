package util.colors

inline class HexColor(private val hex: Long): Color {
    override fun toHex(): HexColor {
        return this
    }

    override fun toRgba(): RGBAColor {
        var color = hex
        val a = color and 0xFF
        color = color - a shr 8
        val b = color and 0xFF
        color = color - b shr 8
        val g = color and 0xFF
        color = color - g shr 8
        val r = color and 0xFF
        return RGBAColor(r / 255.0, g / 255.0, b / 255.0, a / 255.0)
    }

    override fun lighten(amount: Int): HexColor {
        return HexColor(adjustShade(amount))
    }

    override fun darken(amount: Int): HexColor {
        return HexColor(adjustShade(-amount))
    }

    private fun adjustShade(amount: Int): Long {
        fun adjust(shift: Int): Long {
            val shifted = (hex shr shift and 0xFF)
            val willOverflow = amount > 0 && shifted > 0xFF - amount
            val willUnderflow = amount < 0 && shifted < 0x00 - amount
            return if (willOverflow) 0xFF else if (willUnderflow) 0x00 else shifted + amount
        }

        val r = adjust(24)
        val g = adjust(16)
        val b = adjust(8)
        val a = (hex and 0xFF)

        return a or (b shl 8) or (g shl 16) or (r shl 24)
    }

    fun toLong() = hex

    override fun toString(): String {
        return String.format("0x%08X", hex)
    }
}
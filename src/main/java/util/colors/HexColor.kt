package util.colors

inline class HexColor(private val hex: Long): Color {
    override val a: Double
        get() = hex2Alpha(hex)
    override val r: Double
        get() = hex2Red(hex)
    override val g: Double
        get() = hex2Green(hex)
    override val b: Double
        get() = hex2Blue(hex)

    override fun toLong() = hex

    override fun toString(): String {
        return String.format("0x%08X", hex)
    }
}
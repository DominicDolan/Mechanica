package util.colors

class RGBAColor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) : Color {

    var r: Double = red
        private set
    var g: Double = green
        private set
    var b: Double = blue
        private set
    var a: Double = alpha
        private set

    override fun toRgba(): RGBAColor {
        return this
    }

    override fun toHex(): HexColor {
        val a = (a*255).toLong()
        val b = (b*255).toLong() shl 8
        val g = (g*255).toLong() shl 16
        val r = (r*255).toLong() shl 24
        return HexColor(r + b + g + a)
    }

    override fun lighten(amount: Int): RGBAColor {
        adjustShade(amount)
        return this
    }

    override fun darken(amount: Int): RGBAColor {
        adjustShade(-amount)
        return this
    }

    private fun adjustShade(amount: Int) {
        val hex = toHex().toLong()

        val rHex = ((hex shr 24) + amount)
        val gHex = (hex shr 16 and 0xFF) + amount
        val bHex = (hex shr 8 and 0xFF) + amount

        r = (rHex)/255.0
        g = (gHex)/255.0
        b = (bHex)/255.0
    }

    operator fun component1() = r
    operator fun component2() = g
    operator fun component3() = b
    operator fun component4() = a

    override fun toString(): String {
        return "r:$r g:$g b:$b a:$a"
    }
}
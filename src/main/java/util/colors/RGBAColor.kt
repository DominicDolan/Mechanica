package util.colors

class RGBAColor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) : Color {

    override val r: Double = red
    override val g: Double = green
    override val b: Double = blue
    override val a: Double = alpha

    override fun toLong(): Long {
        return rgba2Hex(r,g,b,a)
    }

    override fun toString(): String {
        return "r:$r g:$g b:$b a:$a"
    }
}
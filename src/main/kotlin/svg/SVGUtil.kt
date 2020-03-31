package svg

import java.lang.Long.parseLong
import java.lang.Long.toHexString
import kotlin.math.roundToLong

fun parseColor(hex: String, opacity: String): Long {
    return try {
        val opacityHex = toHexString(((opacity.toFloat() * 255.0f).roundToLong()))
        parseLong(hex.replace("#", "") + opacityHex, 16)
    } catch (e: NumberFormatException) { 0 }
}


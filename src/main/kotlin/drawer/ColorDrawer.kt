package drawer

import util.colors.Color
import util.colors.hex

interface ColorDrawer : Drawer, Color {
    fun get(): Color

    operator fun invoke(color: Color): Drawer

    operator fun invoke(hex: Long): Drawer = invoke(hex(hex))

}

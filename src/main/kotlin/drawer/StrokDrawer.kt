package drawer

interface StrokeDrawer : Drawer {
    operator fun invoke(stroke: Double): Drawer
}
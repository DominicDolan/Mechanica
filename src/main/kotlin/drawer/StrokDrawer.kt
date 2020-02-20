package drawer

interface StrokeDrawer {
    operator fun invoke(stroke: Double): Drawer
}
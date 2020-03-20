package display

interface IDisplay {
    val width: Int
    val height: Int
    val fullscreen: Boolean
    val borderless: Boolean
    val refreshRate: Int
    val title: String
    val monitor: Long
    val window: Long

    val contentScaleX: Float
    val contentScaleY: Float
}
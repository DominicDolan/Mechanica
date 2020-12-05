package com.mechanica.engine.display

interface DrawSurface {
    val width: Int
        get() = resolution.width.toInt()
    val height: Int
        get() = resolution.height.toInt()

    val resolution: Resolution

    var vSync: Boolean

    fun addOnChangedCallback(callback: (DesktopWindow) -> Unit)

    fun update(): Boolean

    fun destroy()

    fun shouldClose()

}
package com.mechanica.engine.display

import com.mechanica.engine.context.loader.DisplayLoader
import com.mechanica.engine.context.loader.GLLoader


interface Monitor {
    val id: Long
    val name: String
    val size: Size
    val contentScale: ContentScale

    val width: Int
    val height: Int

    data class Size(val width_mm: Int, val height_mm: Int)
    data class ContentScale(val xScale: Float, val yScale: Float)

    companion object {
        val allMonitors: Array<out Monitor>
            get() = DisplayLoader.allMonitors

        fun getPrimaryMonitor(): Monitor {
            return DisplayLoader.getPrimaryMonitor()
        }
    }
}
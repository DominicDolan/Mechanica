package game.configuration

import debug.DebugConfiguration
import display.Monitor
import display.Window
import game.view.ResolutionConverter
import game.configuration.ConfigurationData.Companion.emptyLoadeState
import game.configuration.ConfigurationData.Companion.emptyState
import input.ControlsMap
import state.LoadState
import state.State

class GameSetup(data: NullableConfigurationData) : ConfigurationData {
    override val monitor = Monitor.getPrimaryMonitor()
    
    override val title: String = data.title ?: "Mechanica"
    override val resolutionWidth: Int
    override val resolutionHeight: Int
    override val viewWidth: Double
    override val viewHeight: Double
    override val viewX: Double = data.viewX ?: 0.0
    override val viewY: Double = data.viewX ?: 0.0
    override val saveData: Array<Any> = data.saveData ?: emptyArray()
    override val controlsMap: ControlsMap = data.controlsMap ?: object : ControlsMap() { }
    override val fullscreen: Boolean = data.fullscreen ?: false
    override val startingState: (() -> State) = data.startingState ?: { emptyState() }
    override val loadState: (() -> LoadState) = data.loadState ?: { emptyLoadeState() }
    override val windowConfiguration: (Window.() -> Unit) = data.windowConfiguration ?: { }
    override val debugConfiguration: (DebugConfiguration.() -> Unit) = data.debugConfiguration ?: { }

    val ratio: Double

    val window: Window
    val debugConfig = DebugConfiguration()

    val resolutionConverter: ResolutionConverter

    init {

        val resolutionWasSet = data.resolutionWidth != null && data.resolutionHeight != null

        val resolution = setResolution(resolutionWasSet, data)
        resolutionWidth = resolution.first
        resolutionHeight = resolution.second

        ratio = resolutionHeight.toDouble()/resolutionWidth.toDouble()

        window = if (fullscreen && !resolutionWasSet) {
            Window.create(title, monitor)
        } else if (fullscreen && resolutionWasSet) {
            Window.create(title, resolutionWidth, resolutionHeight, monitor)
        } else {
            Window.create(title, resolutionWidth, resolutionHeight)
        }

        resolutionConverter = ResolutionConverter(resolutionWidth, resolutionHeight)

        resolutionConverter.viewWidth = data.viewWidth
        resolutionConverter.viewHeight = data.viewHeight

        resolutionConverter.calculate()
        viewWidth = resolutionConverter.viewWidthOut
        viewHeight = resolutionConverter.viewHeightOut

        windowConfiguration(window)
        debugConfiguration(debugConfig)
    }

    private fun setResolution(resolutionWasSet: Boolean, data: NullableConfigurationData): Pair<Int, Int> {
        val defaultResolutionWidth = (monitor.currentVideoMode.width()*0.75).toInt()
        val defaultResolutionHeight = (monitor.currentVideoMode.height()*0.75).toInt()

        return Pair(
                if (resolutionWasSet) data.resolutionWidth ?: defaultResolutionWidth
                else defaultResolutionWidth,
                if (resolutionWasSet) data.resolutionHeight ?: defaultResolutionHeight
                else defaultResolutionHeight
        )
    }
}
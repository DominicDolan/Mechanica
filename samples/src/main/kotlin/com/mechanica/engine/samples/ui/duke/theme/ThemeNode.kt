package com.mechanica.engine.samples.ui.duke.theme


abstract class ThemeNode() : Style() {
    abstract fun hook(themeHook: String, builder: ThemeNode.() -> Unit): ThemeNode
}
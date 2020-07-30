package com.mechanica.engine.config

import com.mechanica.engine.configuration.Configurable
import com.mechanica.engine.context.DesktopApplication

fun <T> Configurable<T>.configure(setup: T.() -> Unit) {
    this.configureAs(DesktopApplication(), setup)
}
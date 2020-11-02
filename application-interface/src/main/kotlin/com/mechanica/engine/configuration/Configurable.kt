package com.mechanica.engine.configuration

import com.mechanica.engine.context.Application

interface Configurable<T> {
    fun configureAs(application: Application, configure: T.() -> Unit)
}
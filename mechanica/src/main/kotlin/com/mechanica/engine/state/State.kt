package com.mechanica.engine.state

import com.mechanica.engine.drawer.Drawer

/**
 * Created by domin on 22 Mar 2017.
 */

abstract class State {
    abstract fun update(delta: Double)

    abstract fun render(draw: Drawer)
}

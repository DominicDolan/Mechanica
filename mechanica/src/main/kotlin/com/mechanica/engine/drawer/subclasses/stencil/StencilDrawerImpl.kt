package com.mechanica.engine.drawer.subclasses.stencil

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

class StencilDrawerImpl(drawer: Drawer, val state: DrawState) : StencilDrawer(drawer, state)
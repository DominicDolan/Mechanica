package com.mechanica.engine.drawer.shader

open class DrawerShader(
        override val vertex: DrawerScript,
        override val fragment: DrawerScript,
        override val tessellation: DrawerScript? = null,
        override val geometry: DrawerScript? = null): AbstractDrawerShader()
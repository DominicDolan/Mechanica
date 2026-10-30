@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.samples

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.shaders.text.Text
import com.mechanica.nativebackend.NativeApplication
import com.mechanica.nativebackend.NativeFileFactory
import com.mechanica.nativebackend.NativeFontFactory
import platform.posix.F_OK
import platform.posix.access

fun engineMain() {
    val assetRoot = listOf("mechanica/src/main/resources", "../../mechanica/src/main/resources")
        .firstOrNull { access("$it/res/fonts/roboto/Roboto-Regular.ttf", F_OK) == 0 }
        ?: error("Unable to find the engine font resources")
    val fonts = NativeFontFactory()
    val application = NativeApplication(
        fontFactory = fonts,
        assetRoot = assetRoot,
        persistenceRoot = "/tmp/mechanica-native-demo",
        persistencePath = "demo/persistence.json",
    )
    Game.configureAs(application) {
        setViewport(height = 10.0)
        setResolution(960, 540)
        setFullscreen(false)
    }
    val draw = Drawer.create()
    val fontBytes = NativeFileFactory(assetRoot, "/tmp").resource("res/fonts/roboto/Roboto-Regular.ttf").readBytes()
    val sdf = fonts.font(fontBytes) {
        width = 2048
        height = 1024
        characterSize = 96f
        configureSDF { weightAdjustment = 0.6 }
    }
    val sdfText = Text("Signed distance field text", sdf)
    Game.loop {
        draw.green.rectangle(-2.0, -1.0, 4.0, 2.0)
        draw.white.text("Native bitmap font", 0.75, -3.0, 2.0)
        draw.white.text(sdfText, 0.75, -3.0, -2.0)
    }
}

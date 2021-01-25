package com.mechanica.engine.text

interface FontAtlasConfiguration {
    var width: Int
    var height: Int
    var characterSize: Float
    var padding: Float
    var charRange: CharRange
    fun configureSDF(block: SDFConfiguration.() -> Unit)

    class SDFConfiguration {
        var start = -20.0
        var end = 20.0
    }
}


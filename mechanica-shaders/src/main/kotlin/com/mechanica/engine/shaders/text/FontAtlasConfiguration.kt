package com.mechanica.engine.text

interface FontAtlasConfiguration {
    var width: Int
    var height: Int
    var characterSize: Float
    /** Transparent gutter in atlas pixels. Also limits safe bitmap mip levels. */
    var padding: Float
    var charRange: CharRange
    fun configureSDF(block: SDFConfiguration.() -> Unit)

    class SDFConfiguration {
        /** Signed distances in atlas pixels; the range must straddle zero. */
        var start = -4.0
        var end = 4.0

        /** Contour expansion in atlas pixels. Positive values make strokes heavier without changing layout. */
        var weightAdjustment = 0.0
    }
}


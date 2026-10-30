package com.mechanica.engine.text


class LwjglFontAtlasConfiguration(initializer: FontAtlasConfiguration.() -> Unit) : FontAtlasConfiguration {
    val anyWasSet: Boolean
        get() = dimensionsWereSet || paddingWasSet || characterSizeWasSet || distanceFieldsWasSet

    var dimensionsWereSet = false
        private set
    override var width = 1024
        set(value) {
            field = value
            dimensionsWereSet = true
        }
    override var height = 1024
        set(value) {
            field = value
            dimensionsWereSet = true
        }

    var characterSizeWasSet = false
        private set
    override var characterSize = 48f
        set(value) {
            field = value
            characterSizeWasSet = true
        }

    var paddingWasSet = false
        private set
    override var padding = 8f
        // SDF glyphs contain their distance range and use no mips, so only a small
        // extra sampling gutter is needed. Keep existing large-range SDF atlases fitting.
        get() = if (distanceFieldsWasSet && !paddingWasSet) 2f else field
        set(value) {
            field = value
            paddingWasSet = true
        }

    override var charRange = 32.toChar()..128.toChar()

    var distanceFieldsWasSet = false
    val sdfConfiguration = FontAtlasConfiguration.SDFConfiguration()
    override fun configureSDF(block: FontAtlasConfiguration.SDFConfiguration.() -> Unit) {
        distanceFieldsWasSet = true
        block(sdfConfiguration)
    }

    init {
        initializer(this)
        require(width in 1..32767 && height in 1..32767) { "Font atlas dimensions must be between 1 and 32767" }
        require(characterSize.isFinite() && characterSize > 0f) { "Font character size must be positive and finite" }
        require(padding.isFinite() && padding >= 1f && padding <= minOf(width, height) / 2f) {
            "Font padding must be at least one pixel and fit inside the atlas"
        }
        require(!charRange.isEmpty()) { "Font character range must not be empty" }
        if (distanceFieldsWasSet) {
            require(sdfConfiguration.start.isFinite() && sdfConfiguration.end.isFinite() &&
                    (sdfConfiguration.end - sdfConfiguration.start).isFinite() &&
                    sdfConfiguration.start < 0.0 && sdfConfiguration.end > 0.0) {
                "SDF distance range must be finite and straddle zero (start < 0 < end)"
            }
            require(sdfConfiguration.weightAdjustment.isFinite()) { "SDF weight adjustment must be finite" }
        }
    }

    fun reset() {
        paddingWasSet = false
        characterSizeWasSet = false
        distanceFieldsWasSet = false
        dimensionsWereSet = false
    }

}

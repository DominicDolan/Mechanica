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
    override var characterSize = 100f
        set(value) {
            field = value
            characterSizeWasSet = true
        }

    var paddingWasSet = false
        private set
    override var padding = 2f
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
    }

    fun reset() {
        paddingWasSet = false
        characterSizeWasSet = false
        distanceFieldsWasSet = false
        dimensionsWereSet = false
    }

}
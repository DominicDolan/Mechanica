package com.mechanica.engine.gl.models

import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.utils.Image
import com.mechanica.engine.gl.vbo.Bindable

class ImageModel(image: Image, vararg inputs: Bindable)
    : Model(image, *inputs) {

    var image: Image
        get() = inputs[0] as Image
        set(value) {
            inputs[0] = value as Bindable
        }


}
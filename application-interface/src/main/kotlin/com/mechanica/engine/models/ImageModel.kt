package com.mechanica.engine.models

import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray

class ImageModel(image: Image, vararg inputs: Bindable)
    : Model(image, *inputs) {

    constructor(image: Image)
            : this(image,
            AttributeArray.createFrom(Attribute.location(0).vec3()).createUnitQuad(),
            AttributeArray.createFrom(Attribute.location(1).vec2()).createInvertedUnitQuad()
    )

    var image: Image
        get() = inputs[0] as Image
        set(value) {
            inputs[0] = value as Bindable
        }


}
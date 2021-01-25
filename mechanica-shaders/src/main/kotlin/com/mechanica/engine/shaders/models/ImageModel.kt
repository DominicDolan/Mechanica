package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.unit.vector.VectorShapes

class ImageModel(image: Image, vararg inputs: Bindable)
    : Model(image, *inputs) {

    constructor(image: Image)
            : this(image,
            AttributeArray.createPositionArray(VectorShapes.createUnitSquare()),
            AttributeArray.createTextureArray(VectorShapes.createInvertedUnitSquare())
    )

    var image: Image
        get() = inputs[0] as Image
        set(value) {
            inputs[0] = value as Bindable
        }


}
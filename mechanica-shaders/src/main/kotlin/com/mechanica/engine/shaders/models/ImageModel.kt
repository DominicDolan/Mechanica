package com.mechanica.engine.shaders.models

import com.cave.library.vector.arrays.Vector2Arrays
import com.mechanica.engine.shaders.attributes.AttributeArray

class ImageModel(image: Image, vararg inputs: Bindable)
    : Model(image, *inputs) {

    constructor(image: Image)
            : this(image,
            AttributeArray.createPositionArray(Vector2Arrays.createUnitSquare()),
            AttributeArray.createTextureArray(Vector2Arrays.createInvertedUnitSquare())
    )

    var image: Image
        get() = inputs[0] as Image
        set(value) {
            inputs[0] = value as Bindable
        }


}
package gl.models

import gl.vbo.Bindable
import graphics.Image

class ImageModel(image: Image, vararg inputs: Bindable)
    : Model(image, *inputs) {

    var image: Image
        get() = inputs[0] as Image
        set(value) {
            inputs[0] = value as Bindable
        }


}
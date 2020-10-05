package com.mechanica.engine.duke.elements

import com.mechanica.engine.duke.context.DukeUIScene
import com.mechanica.engine.models.Image

class ImageElement(scene: DukeUIScene) : Element(scene) {
    var image: Image? = null
    override val elementDrawer = ElementDrawer { draw, _ ->
        val image = this@ImageElement.image
        if (image != null) {
            draw.ui.image(image, x, y + height, width, -height)
        }
    }

    inline fun addChildToImageElement(image: Image?, block: ImageElement.() -> Unit) {
        val oldNode = updateForNextNode(node)
        val oldImage = this.image
        this.image = image

        block(this)
        node.renderOnce()

        this.node = oldNode
        this.image = oldImage
    }
}
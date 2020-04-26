package drawer.subclasses.transformation

import drawer.DrawData
import drawer.Drawer2

class TransformationDrawerImpl(drawer: Drawer2, private val data: DrawData): TransformationDrawer, Drawer2 by drawer {
    override fun translate(x: Number, y: Number): TransformationDrawer {
        data.setTranslate(x.toFloat(), y.toFloat())
        return this
    }

    override fun scale(x: Number, y: Number): TransformationDrawer {
        data.setScale(x.toFloat(), y.toFloat())
        return this
    }

}
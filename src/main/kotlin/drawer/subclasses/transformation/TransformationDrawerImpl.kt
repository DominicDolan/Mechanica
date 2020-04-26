package drawer.subclasses.transformation

import drawer.DrawData
import drawer.Drawer

class TransformationDrawerImpl(drawer: Drawer, private val data: DrawData): TransformationDrawer, Drawer by drawer {
    override fun translate(x: Number, y: Number): TransformationDrawer {
        data.setTranslate(x.toFloat(), y.toFloat())
        return this
    }

    override fun scale(x: Number, y: Number): TransformationDrawer {
        data.setScale(x.toFloat(), y.toFloat())
        return this
    }

}
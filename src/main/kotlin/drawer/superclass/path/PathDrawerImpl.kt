package drawer.superclass.path

import drawer.DrawData
import drawer.Drawer2
import drawer.shader.DrawerRenderer
import drawer.shader.PathRenderer
import org.joml.Matrix4f
import util.units.Vector

class PathDrawerImpl(
        private val data: DrawData,
        private val drawerRenderer: DrawerRenderer): PathDrawer {

    private val renderer = PathRenderer()
    private val transformation = Matrix4f().identity()

    override fun path(path: Array<Vector>) {
        renderer.fillFloats(path)
        draw()
    }

    override fun path(path: List<Vector>) {
        renderer.fillFloats(path)
        draw()
    }

    private fun draw() {
        data.getTransformationMatrix(transformation)
        renderer.color = data.strokeColor
        renderer.stroke = data.strokeWidth.toFloat()
        renderer.render(transformation)
    }
}
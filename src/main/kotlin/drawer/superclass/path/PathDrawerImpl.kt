package drawer.superclass.path

import drawer.DrawData
import drawer.shader.DrawerRenderer
import drawer.shader.PathRenderer
import org.joml.Matrix4f
import util.units.DynamicVector
import util.units.Vector

class PathDrawerImpl(
        private val data: DrawData,
        private val drawerRenderer: DrawerRenderer): PathDrawer {

    private val renderer = PathRenderer()
    private val transformation = Matrix4f().identity()
    private val line = ArrayList<DynamicVector>()

    init {
        line.add(DynamicVector.create())
        line.add(DynamicVector.create())
    }

    override fun path(path: Array<Vector>) {
        renderer.fillFloats(path)
        draw()
    }

    override fun path(path: List<Vector>) {
        renderer.fillFloats(path)
        draw()
    }

    override fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        line[0].x = x1.toDouble()
        line[0].y = y1.toDouble()
        line[1].x = x2.toDouble()
        line[1].y = y2.toDouble()
        renderer.fillFloats(line)
        draw()
    }

    private fun draw() {
        data.getTransformationMatrix(transformation)
        renderer.color = data.strokeColor
        renderer.stroke = data.strokeWidth.toFloat()
        renderer.render(transformation)
    }
}
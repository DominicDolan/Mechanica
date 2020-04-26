package drawer.superclass.path

import drawer.Drawer2
import util.units.LightweightVector
import util.units.Vector

interface PathDrawer {
    fun path(path: Array<Vector>)
    fun path(path: List<Vector>)
}
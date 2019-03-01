package world

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World

/**
 * Created by domin on 01/11/2017.
 */
class World {
    val world: org.jbox2d.dynamics.World = World(Vec2(0f,-9.81f))
}
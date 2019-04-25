package world

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.joints.Joint
import org.jbox2d.dynamics.joints.JointDef

/**
 * Created by domin on 01/11/2017.
 */
class World {
    companion object {
        var world: org.jbox2d.dynamics.World = World(Vec2(0f,-9.81f))

        fun createBody(def: BodyDef): Body {
            return world.createBody(def)
        }

        fun createJoint(def: JointDef): Joint {
            return world.createJoint(def)
        }
    }
}
package util.extensions

import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.joints.Joint

fun Array<out Body>.setMaskBits(i: Int) {
    this.forEach { it.setMaskBits(i) }
}

fun Body.setMaskBits(i: Int) {
    this.m_fixtureList.forEach { it.m_filter.maskBits = i; it.refilter() }
}

fun Fixture.forEach(operation: (f: Fixture) -> Unit) {
    var f: Fixture? = this
    while (f != null) {
        operation(f)
        f = f.m_next
    }
}

fun World.clear() {
    var body: Body? = this.bodyList
    while (body != null) {
        val nextBody = body.m_next
        this.destroyBody(body)
        body = nextBody
    }
    
    var joint: Joint? = this.jointList
    while (joint != null) {
        val nextJoint = joint.m_next
        this.destroyJoint(joint)
        joint = nextJoint
    }
}
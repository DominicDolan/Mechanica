package physics

import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.contacts.Contact

object ContactEvents : ContactListener {
    private val map =  HashMap<Any, Pair<() -> Unit, () -> Unit>>()
    private var bodyId = 0

    fun addEvent(body: Body, beginContact: () -> Unit, endContact: () -> Unit) {
        body.userData = bodyId
        map[bodyId] = Pair(beginContact, endContact)
        bodyId++
    }

    fun addEvent(fixture: Fixture, beginContact: () -> Unit, endContact: () -> Unit) {
        fixture.userData = bodyId
        map[bodyId] = Pair(beginContact, endContact)
        bodyId++
    }

    override fun beginContact(contact: Contact?) {
        if (contact != null) {
//            println("User Data A: ${contact.fixtureA.body.userData}")
//            println("User Data B: ${contact.fixtureB.body.userData}")
            val lamdaA = map[contact.fixtureA.body.userData]
            val lamdaB = map[contact.fixtureB.body.userData]

            if (lamdaA != null) {
                lamdaA.first()
            }
            if (lamdaB != null) {
                lamdaB.first()
            }
        }
    }

    override fun endContact(contact: Contact?) {
        if (contact != null) {
            val lamdaA = map[contact.fixtureA.body.userData]
            val lamdaB = map[contact.fixtureB.body.userData]

            if (lamdaA != null) {
                lamdaA.second()
            }
            if (lamdaB != null) {
                lamdaB.second()
            }
        }
    }

    override fun preSolve(contact: Contact?, manifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, contactImpulse: ContactImpulse?) {

    }
}
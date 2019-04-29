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
            val lamdaBodyA = map[contact.fixtureA.body.userData]
            val lamdaBodyB = map[contact.fixtureB.body.userData]

            val lamdaFixtureA = map[contact.fixtureA.userData]
            val lamdaFixtureB = map[contact.fixtureB.userData]

            if (lamdaBodyA != null) {
                lamdaBodyA.first()
            }
            if (lamdaBodyB != null) {
                lamdaBodyB.first()
            }

            if (lamdaFixtureA != null) {
                lamdaFixtureA.first()
            }
            if (lamdaFixtureB != null) {
                lamdaFixtureB.first()
            }
        }
    }

    override fun endContact(contact: Contact?) {
        if (contact != null) {
            val lamdaBodyA = map[contact.fixtureA.body.userData]
            val lamdaBodyB = map[contact.fixtureB.body.userData]

            val lamdaFixtureA = map[contact.fixtureA.userData]
            val lamdaFixtureB = map[contact.fixtureB.userData]

            if (lamdaBodyA != null) {
                lamdaBodyA.second()
            }
            if (lamdaBodyB != null) {
                lamdaBodyB.second()
            }

            if (lamdaFixtureA != null) {
                lamdaFixtureA.second()
            }
            if (lamdaFixtureB != null) {
                lamdaFixtureB.second()
            }
        }
    }

    override fun preSolve(contact: Contact?, manifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, contactImpulse: ContactImpulse?) {

    }
}
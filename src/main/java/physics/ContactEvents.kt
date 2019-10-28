package physics

import debug.DebugDrawer
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.WorldManifold
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.Fixture
import org.jbox2d.dynamics.contacts.Contact
import util.extensions.toVec

object ContactEvents : ContactListener {
    private val map =  HashMap<Any, Pair<(Contact) -> Unit, (Contact) -> Unit>>()
    private var bodyId = 0

    fun addEvent(body: Body, beginContact: (Contact) -> Unit, endContact: (Contact) -> Unit) {
        body.userData = bodyId
        map[bodyId] = Pair(beginContact, endContact)
        bodyId++
    }

    fun addEvent(fixture: Fixture, beginContact: (Contact) -> Unit, endContact: (Contact) -> Unit) {
        fixture.userData = bodyId
        map[bodyId] = Pair(beginContact, endContact)
        bodyId++
    }

    @Suppress("IfThenToSafeAccess")
    override fun beginContact(contact: Contact?) {
        if (contact != null) {
            val lambdaBodyA = map[contact.fixtureA.body.userData]
            val lambdaBodyB = map[contact.fixtureB.body.userData]

            val lambdaFixtureA = map[contact.fixtureA.userData]
            val lambdaFixtureB = map[contact.fixtureB.userData]

            if (lambdaBodyA != null) {
                lambdaBodyA.first(contact)
            }
            if (lambdaBodyB != null) {
                lambdaBodyB.first(contact)
            }

            if (lambdaFixtureA != null) {
                lambdaFixtureA.first(contact)
            }
            if (lambdaFixtureB != null) {
                lambdaFixtureB.first(contact)
            }
        }
    }

    @Suppress("IfThenToSafeAccess")
    override fun endContact(contact: Contact?) {
        if (contact != null) {
            val lambdaBodyA = map[contact.fixtureA.body.userData]
            val lambdaBodyB = map[contact.fixtureB.body.userData]

            val lambdaFixtureA = map[contact.fixtureA.userData]
            val lambdaFixtureB = map[contact.fixtureB.userData]

            if (lambdaBodyA != null) {
                lambdaBodyA.second(contact)
            }
            if (lambdaBodyB != null) {
                lambdaBodyB.second(contact)
            }

            if (lambdaFixtureA != null) {
                lambdaFixtureA.second(contact)
            }
            if (lambdaFixtureB != null) {
                lambdaFixtureB.second(contact)
            }
        }
    }

    override fun preSolve(contact: Contact?, manifold: Manifold?) {

    }

    override fun postSolve(contact: Contact?, contactImpulse: ContactImpulse?) {

    }
}
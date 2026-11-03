package com.mechanica.engine.physics

import com.cave.library.vector.vec2.Vector2
import com.cave.library.angle.radians
import kotlin.test.*

class TerrainChainTest {
    @Test fun openChainKeepsBothEndEdgesAndOwnsLifetime() {
        val world=PhysicsWorld(Vector2.create(0.0,-10.0))
        try {
            // Right-to-left has an upward-facing front side.
            val terrain=world.createTerrainChain(listOf(Vector2.create(6.0,0.0),Vector2.create(0.0,0.0),Vector2.create(-6.0,0.0)))
            assertEquals(2.0,world.groundDistance(Vector2.create(5.5,2.0),3.0)!!,0.001)
            assertEquals(2.0,world.groundDistance(Vector2.create(-5.5,2.0),3.0)!!,0.001)
            assertNull(world.groundDistance(Vector2.create(7.0,2.0),3.0))
            val body=world.createDynamicCircle(Vector2.create(5.5,2.0),0.5)
            repeat(120) { world.step(1.0/60) }
            assertEquals(0.5,body.position.y,0.06)
            terrain.destroy()
            assertNull(world.groundDistance(Vector2.create(0.0,2.0),3.0))
        } finally { world.close() }
    }
    @Test fun explicitGhostsProvideContinuityWithoutAddingCollisionEdges() {
        val world=PhysicsWorld(Vector2.create(0.0,-10.0))
        try {
            world.createTerrainChain(listOf(Vector2.create(4.0,1.0),Vector2.create(0.0,0.0)),
                previousGhost=Vector2.create(8.0,0.0),nextGhost=Vector2.create(-4.0,0.0))
            assertNotNull(world.groundDistance(Vector2.create(2.0,3.0),4.0))
            assertNull(world.groundDistance(Vector2.create(6.0,3.0),4.0))
            assertNull(world.groundDistance(Vector2.create(-2.0,3.0),4.0))
            assertFailsWith<IllegalArgumentException>{
                world.createTerrainChain(listOf(Vector2.create(4.0,1.0),Vector2.create(0.0,0.0)),previousGhost=Vector2.create(4.0,1.0))
            }
        }finally{world.close()}
    }
    @Test fun teleportWakesSleepingBodyAndGravityResumes() {
        val world=PhysicsWorld(Vector2.create(0.0,-10.0))
        try {
            world.createTerrainChain(listOf(Vector2.create(10.0,0.0),Vector2.create(-10.0,0.0)))
            val body=world.createDynamicCircle(Vector2.create(0.0,2.0),0.5)
            repeat(360){world.step(1.0/60)}
            body.setTransform(Vector2.create(0.0,5.0),0.0.radians)
            body.linearVelocity.set(0.0,0.0)
            repeat(30){world.step(1.0/60)}
            assertTrue(body.position.y<4.0,"Teleported bodies must resume gravity even with zero initial velocity")
        }finally{world.close()}
    }
    @Test fun chainValidationPrecedesNativeCreation() {
        val world=PhysicsWorld(Vector2.create(0.0,-10.0))
        try {
            assertFailsWith<IllegalArgumentException> { world.createTerrainChain(listOf(Vector2.create(0.0,0.0))) }
            assertFailsWith<IllegalArgumentException> { world.createTerrainChain(listOf(Vector2.create(0.0,0.0),Vector2.create(0.0,0.0))) }
            assertFailsWith<IllegalArgumentException> { world.createTerrainChain(listOf(Vector2.create(0.0,0.0),Vector2.create(Double.NaN,0.0))) }
        } finally { world.close() }
        assertFailsWith<IllegalStateException> { world.createTerrainChain(listOf(Vector2.create(1.0,0.0),Vector2.create(0.0,0.0))) }
    }
}

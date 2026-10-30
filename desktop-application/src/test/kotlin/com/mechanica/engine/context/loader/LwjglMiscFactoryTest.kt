package com.mechanica.engine.context.loader

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private var fixtureProperty = 0

class LwjglMiscFactoryTest {
    private var memberProperty = 0

    @Test
    fun preservesJvmPersistencePropertyNames() {
        val factory = LwjglMiscFactory()
        assertEquals("${javaClass.canonicalName}.memberProperty", factory.persistencePropertyName(this, this::memberProperty))
        assertEquals("com.mechanica.engine.context.loader.LwjglMiscFactoryTestKt.fixtureProperty", factory.persistencePropertyName(null, ::fixtureProperty))
    }

    @Test
    fun publishesBackgroundFailure() {
        val started = CountDownLatch(1)
        val error = IllegalStateException("test")
        val task = LwjglMiscFactory().startBackgroundTask {
            started.countDown()
            throw error
        }
        assertTrue(started.await(5, TimeUnit.SECONDS))
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (!task.isFinished && System.nanoTime() < deadline) Thread.yield()
        assertTrue(task.isFinished)
        assertSame(error, task.failure)
    }
}

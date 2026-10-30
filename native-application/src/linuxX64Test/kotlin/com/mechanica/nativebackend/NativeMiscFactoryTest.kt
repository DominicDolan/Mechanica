@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import kotlin.test.*
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.seconds
import platform.posix.usleep

class NativeMiscFactoryTest {
    @Test
    fun backgroundTaskPublishesFailure() {
        val failure = IllegalStateException("expected")
        val task = NativeMiscFactory("test.json").startBackgroundTask { throw failure }
        val start = TimeSource.Monotonic.markNow()
        while (!task.isFinished && start.elapsedNow() < 5.seconds) usleep(1000u)
        assertTrue(task.isFinished)
        assertSame(failure, task.failure)
    }
}

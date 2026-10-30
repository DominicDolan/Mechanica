package com.mechanica.engine.utils

import org.lwjgl.BufferUtils
import java.nio.ByteBuffer

internal fun ByteArray.toDirectBuffer(): ByteBuffer = BufferUtils.createByteBuffer(size).put(this).flip()

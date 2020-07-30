package com.mechanica.engine.memory

import com.mechanica.engine.unit.vector.DynamicIntVector
import java.nio.ByteBuffer

class BitmapBuffer(val buffer: ByteBuffer, val width: Int, val height: Int) {

    val bufferPosition = object : DynamicIntVector {
        private var _x = 0
        override var x: Int
            get() = _x
            set(value) {
                set(value, _y)
            }
        
        private var _y = 0
        override var y: Int
            get() = _y
            set(value) {
                set(_x, value)
            }

        override fun set(x: Int, y: Int) {
            _x = x
            _y = y
            val row = y*width
            buffer.position(row+x)
        }
    }
    
    val stBufferPosition = STVector()
    
    fun advanceBy(advance: Int) {
        buffer.position(buffer.position() + advance)
    }
    
    fun insert(other: ByteBuffer, otherWidth: Int, s: Float, t: Float) {
        stBufferPosition.set(s, t)
        insert(other, otherWidth)
    }
    
    fun insert(other: ByteBuffer, otherWidth: Int, x: Int, y: Int) {
        bufferPosition.set(x, y)
        insert(other, otherWidth)
    }

    fun insert(other: BitmapBuffer, s: Float, t: Float) {
        stBufferPosition.set(s, t)
        other.zero()

        insert(other.buffer, other.width)
    }

    fun insert(other: BitmapBuffer, x: Int, y: Int) {
        bufferPosition.set(x, y)
        other.zero()

        insert(other.buffer, other.width)
    }

    fun zero() {
        buffer.position(0)
    }

    private fun insert(other: ByteBuffer, otherWidth: Int) {
        var j = 0
        val limit = other.limit()
        while (other.position() < limit - otherWidth) {
            other.limit((j + 1)*otherWidth - 1)
            other.position(j++*otherWidth)
            buffer.put(other)
            advanceBy(width - otherWidth + 1)
        }
    }
    private fun insertOld(other: ByteBuffer, otherWidth: Int) {
        var j = 0
        while (other.hasRemaining()) {
            var i = 0
            while (i++ < otherWidth) {
                buffer.put(other.get())
            }
            advanceBy(width - otherWidth)
            j++
        }
    }

    inner class STVector {
        private var _s = 0f
        var s: Float
            get() = _s
            set(value) {
                set(value, _t)
            }

        private var _t = 0f
        var t: Float
            get() = _t
            set(value) {
                set(_s, value)
            }

        fun set(s: Float, t: Float) {
            _s = s
            _t = t
            val x = (s*width).toInt()
            val y = (t*height).toInt()
            bufferPosition.set(x, y)
        }
    }
}
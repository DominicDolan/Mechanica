package com.mechanica.engine.gl.context

import org.lwjgl.opengl.GL40
import java.nio.FloatBuffer
import java.nio.IntBuffer

class LwjglGL40 : GLGeneric {
    override fun glUniform1f(location: Int, v0: Float) {
        GL40.glUniform1f(location, v0)
    }

    override fun glUniform2f(location: Int, v0: Float, v1: Float) {
        GL40.glUniform2f(location, v0, v1)
    }

    override fun glUniform3f(location: Int, v0: Float, v1: Float, v2: Float) {
        GL40.glUniform3f(location, v0, v1, v2)
    }

    override fun glUniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float) {
        GL40.glUniform4f(location, v0, v1, v2, v3)
    }

    override fun glUniform1i(location: Int, v0: Int) {
        GL40.glUniform1i(location, v0)
    }

    override fun glUniform2i(location: Int, v0: Int, v1: Int) {
        GL40.glUniform2i(location, v0, v1)
    }

    override fun glUniform3i(location: Int, v0: Int, v1: Int, v2: Int) {
        GL40.glUniform3i(location, v0, v1, v2)
    }

    override fun glUniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int) {
        GL40.glUniform4i(location, v0, v1, v2, v3)
    }

    override fun glUniform1fv(location: Int, value: FloatBuffer) {
        GL40.glUniform1fv(location, value)
    }

    override fun glUniform2fv(location: Int, value: FloatBuffer) {
        GL40.glUniform2fv(location, value)
    }

    override fun glUniform3fv(location: Int, value: FloatBuffer) {
        GL40.glUniform3fv(location, value)
    }

    override fun glUniform4fv(location: Int, value: FloatBuffer) {
        GL40.glUniform4fv(location, value)
    }

    override fun glUniform1iv(location: Int, value: IntBuffer) {
        GL40.glUniform1iv(location, value)
    }

    override fun glUniform2iv(location: Int, value: IntBuffer) {
        GL40.glUniform2iv(location, value)
    }

    override fun glUniform3iv(location: Int, value: IntBuffer) {
        GL40.glUniform3iv(location, value)
    }

    override fun glUniform4iv(location: Int, value: IntBuffer) {
        GL40.glUniform4iv(location, value)
    }

    override fun glUniformMatrix2fv(location: Int, transpose: Boolean, value: FloatBuffer) {
        GL40.glUniformMatrix2fv(location, transpose, value)
    }

    override fun glUniformMatrix3fv(location: Int, transpose: Boolean, value: FloatBuffer) {
        GL40.glUniformMatrix3fv(location, transpose, value)
    }

    override fun glUniformMatrix4fv(location: Int, transpose: Boolean, value: FloatBuffer) {
        GL40.glUniformMatrix4fv(location, transpose, value)
    }
}
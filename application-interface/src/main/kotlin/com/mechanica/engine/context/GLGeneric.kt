package com.mechanica.engine.context

import java.nio.FloatBuffer
import java.nio.IntBuffer

interface GLGeneric {

    // --- [ glUniform1f ] ---
    /**
     * Specifies the value of a float uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform1f(location: Int, v0: Float)

    // --- [ glUniform2f ] ---
    /**
     * Specifies the value of a vec2 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform2f(location: Int, v0: Float, v1: Float)

    // --- [ glUniform3f ] ---
    /**
     * Specifies the value of a vec3 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     * @param v2       the uniform z value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform3f(location: Int, v0: Float, v1: Float, v2: Float)

    // --- [ glUniform4f ] ---
    /**
     * Specifies the value of a vec4 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     * @param v2       the uniform z value
     * @param v3       the uniform w value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float)

    // --- [ glUniform1i ] ---
    /**
     * Specifies the value of an int uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform1i(location: Int, v0: Int)

    // --- [ glUniform2i ] ---

    /**
     * Specifies the value of an ivec2 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform2i(location: Int, v0: Int, v1: Int)

    // --- [ glUniform3i ] ---
    /**
     * Specifies the value of an ivec3 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     * @param v2       the uniform z value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform3i(location: Int, v0: Int, v1: Int, v2: Int)

    // --- [ glUniform4i ] ---
    /**
     * Specifies the value of an ivec4 uniform variable for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param v0       the uniform x value
     * @param v1       the uniform y value
     * @param v2       the uniform z value
     * @param v3       the uniform w value
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int)

    // --- [ glUniform1fv ] ---
    /**
     * Specifies the value of a single float uniform variable or a float uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform1fv(location: Int, value: FloatBuffer)

    /**
     * Specifies the value of a single vec2 uniform variable or a vec2 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform2fv(location: Int, value: FloatBuffer)

    /**
     * Specifies the value of a single vec3 uniform variable or a vec3 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform3fv(location: Int, value: FloatBuffer)

    /**
     * Specifies the value of a single vec4 uniform variable or a vec4 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform4fv(location: Int, value: FloatBuffer)

    /**
     * Specifies the value of a single int uniform variable or a int uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform1iv(location: Int, value: IntBuffer)

    /**
     * Specifies the value of a single ivec2 uniform variable or an ivec2 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform2iv(location: Int, value: IntBuffer)

    /**
     * Specifies the value of a single ivec3 uniform variable or an ivec3 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform3iv(location: Int, value: IntBuffer)

    /**
     * Specifies the value of a single ivec4 uniform variable or an ivec4 uniform variable array for the current program object.
     *
     * @param location the location of the uniform variable to be modified
     * @param value    a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniform4iv(location: Int, value: IntBuffer)

    /**
     * Specifies the value of a single mat2 uniform variable or a mat2 uniform variable array for the current program object.
     *
     * @param location  the location of the uniform variable to be modified
     * @param transpose whether to transpose the matrix as the values are loaded into the uniform variable
     * @param value     a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniformMatrix2fv(location: Int, transpose: Boolean, value: FloatBuffer)

    /**
     * Specifies the value of a single mat3 uniform variable or a mat3 uniform variable array for the current program object.
     *
     * @param location  the location of the uniform variable to be modified
     * @param transpose whether to transpose the matrix as the values are loaded into the uniform variable
     * @param value     a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniformMatrix3fv(location: Int, transpose: Boolean, value: FloatBuffer)

    /**
     * Specifies the value of a single mat4 uniform variable or a mat4 uniform variable array for the current program object.
     *
     * @param location  the location of the uniform variable to be modified
     * @param transpose whether to transpose the matrix as the values are loaded into the uniform variable
     * @param value     a pointer to an array of `count` values that will be used to update the specified uniform variable
     *
     * @see [Reference Page](http://docs.gl/gl4/glUniform)
     */
    fun glUniformMatrix4fv(location: Int, transpose: Boolean, value: FloatBuffer)
}
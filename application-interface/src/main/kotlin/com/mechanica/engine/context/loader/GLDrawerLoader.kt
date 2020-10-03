package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Model
import java.nio.IntBuffer

interface GLDrawerLoader {
    /**
     * Defines a sequence of geometric primitives using [model]'s
     * elements, whose indices are stored in the buffer bound to the
     * GL_ELEMENT_ARRAY_BUFFER buffer
     */
    fun drawElements(model: Model)

    /**
     * Behaves identically to glDrawElements() except that the ith element
     * transferred by the corresponding draw command will be taken from
     * element indices`[i]` + basevertex of each enabled vertex attribute array.
     */
    @Suppress("KDocUnresolvedReference")
    fun drawElementsBaseVertex(model: Model, baseVertex: Int)

    /**
     * This is a restricted form of glDrawElements() in that it forms a contract
     * between the application (i.e., you) and OpenGL that guarantees that any
     * index contained in the section of the element array buffer referenced by
     * indices and model.vertexCount will fall within the range specified by start and end.
     */
    fun drawRangeElements(model: Model, start: Int, end: Int)

    /**
     * Forms a contractual agreement between the application similar to that of
     * [drawRangeElements], while allowing the base vertex to be specified in
     * basevertex. In this case, the contract states that the values stored in the
     * element array buffer will fall between start and end before basevertex is added.
     */
    fun drawRangeElementsBaseVertex(model: Model, start: Int, end: Int, baseVertex: Int)

    /**
     * Behaves exactly as [drawElements], except that the parameters for the
     * drawing command are taken from a structure stored in the buffer bound
     * to the GL_DRAW_INDIRECT_BUFFER binding point.
     */
    fun drawElementsIndirect(model: Model)

    /**
     * Draws multiple sets of geometric primitives with a single OpenGL
     * function call.
     */
    fun multiDrawElements(models: Array<Model>)

    /**
     * Draws multiple sets of geometric primitives with a single OpenGL
     * function call. first, indices, and baseVertex are arrays of primcount
     * parameters that would be valid for a call to
     * glDrawElementsBaseVertex().
     */
    fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: IntBuffer)

    /**
     * The same as [multiDrawElementsBaseVertex] but the same [baseVertex] is
     * passed in for every primitive
     */
    fun multiDrawElementsBaseVertex(models: Array<Model>, baseVertex: Int)

    /**
     * Draws primCount instances of the geometric primitives specified by [model]
     * as if specified by individual calls to [drawElements].
     * As with [drawArraysInstanced], the built-in variable gl_InstanceID
     * is incremented for each instance, and new values are presented to the
     * vertex shader for each instanced vertex attribute.
     */
    fun drawElementsInstanced(model: Model, instanceCount: Int)

    /**
     * Draws [instanceCount] instances of the geometric primitives specified by [model]
     * and [baseVertex] as if specified by individual calls to
     * [drawElementsBaseVertex]. As with [drawArraysInstanced], the
     * built-in variable gl_InstanceID is incremented for each instance, and
     * new values are presented to the vertex shader for each instanced vertex
     * attribute.
     */
    fun drawElementsInstancedBaseVertex(model: Model, instanceCount: Int, baseVertex: Int)

    /**
     * Draws [instanceCount] instances of the geometric primitives specified by [model]
     * as if specified by individual calls to [drawElements].
     * As with [drawArraysInstanced], the built-in variable gl_InstanceID
     * is incremented for each instance, and new values are presented to the
     * vertex shader for each instanced vertex attribute. Furthermore, the
     * implied index used to fetch any instanced vertex attributes is offset by
     * the value of [baseInstance] by OpenGL.
     */
    fun drawElementsInstancedBaseInstance(model: Model, instanceCount: Int, baseInstance: Int)


    /**
     * Constructs a sequence of geometric primitives using array elements
     * specified within [model]
     */
    fun drawArrays(model: Model)

    /**
     * Behaves exactly as [drawArraysInstanced], except that the parameters
     * for the drawing command are taken from a structure stored in the buffer
     * bound to the GL_DRAW_INDIRECT_BUFFER binding point (the draw
     * indirect buffer)
     */
    fun drawArraysIndirect(model: Model)

    /**
     * Draws multiple sets of geometric primitives with a single OpenGL
     * function call.
     */
    fun multiDrawArrays(models: Array<Model>)

    /**
     * Draws [instanceCount] instances of the geometric primitives specified by [model]
     * as if specified by individual calls to [drawArrays]. The
     * built-in variable gl_InstanceID is incremented for each instance, and
     * new values are presented to the vertex shader for each instanced vertex
     * attribute.
     */
    fun drawArraysInstanced(model: Model, instanceCount: Int)

    /**
     * Draws [instanceCount] instances of the geometric primitives specified by [model]
     * as if specified by individual calls to [drawArrays]. The
     * built-in variable gl_InstanceID is incremented for each instance, and
     * new values are presented to the vertex shader for each instanced vertex
     * attribute. Furthermore, the implied index used to fetch any instanced
     * vertex attributes is offset by the value of [baseInstance] by OpenGL.
     */
    fun drawArraysInstancedBaseInstance(model: Model, instanceCount: Int, baseInstance: Int)

}
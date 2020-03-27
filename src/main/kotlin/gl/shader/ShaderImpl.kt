package gl.shader

import display.Game
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.joml.Vector3f

internal class ShaderImpl(
        override val vertex: ShaderScript,
        override val fragment: ShaderScript,
        override val tessellation: ShaderScript?,
        override val geometry: ShaderScript?): Shader {

    private val matrixType = vertex.qualifier("uniform").float("matrixType")

    private val projection = vertex.qualifier("uniform").mat4("projection")
    private val transformation = vertex.qualifier("uniform").mat4("transformation")
    private val view = vertex.qualifier("uniform").mat4("view")
    private val pvMatrix = vertex.qualifier("uniform").mat4("pvMatrix")

    private val pixelScale = fragment.qualifier("uniform").float("pixelScale")

    private val vec3 = Vector3f()

    private var _loader: ShaderLoader? = null
    private val loader: ShaderLoader
        get() {
            val l = this._loader
            val loader = l ?: ShaderLoader(vertex, fragment, tessellation, geometry)
            this._loader = loader
            return loader
        }
    override val id: Int
        get() = loader.id

    init {
        //language=GLSL
        vertex.addOther("""
            vec4 matrices(vec4 position) {
                if(matrixType == 0f) {
                    return pvMatrix*transformation*position;
                }
                return projection*view*transformation*position;
            }
        """)
    }

    override fun loadMatrices(transformation: Matrix4f, projection: Matrix4f, view: Matrix4f) {
        matrixType.value = 1f
        if (projection === Game.projectionMatrix.get()) {
            if (view === Game.viewMatrix.get()) {
                matrixType.value = 0f
                this.pvMatrix.set(Game.pvMatrix)
            } else if (view === Game.uiViewMatrix.get()) {
                matrixType.value = 0f
                this.pvMatrix.set(Game.pvUiMatrix)
            }
        }

        this.transformation.set(transformation)
        this.projection.set(projection)
        this.view.set(view)

        val scale = getScale(transformation)*getScale(projection)*getScale(view)
        this.pixelScale.value = 1f/scale

    }

    private fun getScale(mat: Matrix4f): Float {
        mat.getScale(vec3)
        vec3.z = 0f
        return vec3.length()
    }
}
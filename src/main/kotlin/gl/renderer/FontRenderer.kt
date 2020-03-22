package gl.renderer

import display.Game
import font.FontType
import font.GUIText
import font.TextMeshDynamicCreator
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.loadImage
import gl.vbo.AttributeArray
import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import loader.loadFont
import models.Model
import org.joml.Matrix4f
import resources.Res
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class FontRenderer : Renderer {
    override var view: Matrix4f = Game.viewMatrix.get()

    private val vertex = object : ShaderScript() {
        val translation = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D fontAtlas;
                layout (location=2) in vec2 textPositions;

                void main(void) {
                    gl_Position = matrices(vec4(textPositions + $translation, 1.0, 1.0));
                    tc = $textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 color;
                in vec2 tc;
                layout (binding=0) uniform sampler2D fontAtlas;   
                                
                const float edge = 0.5;
                const float border_fraction = 0.2;

                void main(void) {
                    float distance = 1.0 - texture(fontAtlas, tc).a;
                    float alpha = (1.0 - smoothstep(edge, edge + border_fraction*edge, distance))*$color.a;
                    color = vec4($color.rgb, alpha);
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)

    private val model: Model
    private val textPositionAttribute = AttributePointer.create(2, 2)
    private val meshCreator = TextMeshDynamicCreator()
    private val guiText: GUIText
    private val font: FontType = loadFont("arial")

    private val positionVBO: AttributeArray
    private val textureVBO: AttributeArray

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    var text: String
        get() = guiText.textString ?: ""
        set(value) {
            set(text = value)
            updateMesh()
        }

    fun set(text: String = guiText.textString ?: "",
            fontSize: Float = guiText.fontSize,
            font: FontType = guiText.font ?: this.font,
            x: Float = guiText.positionX,
            y: Float = guiText.positionY,
            maxLineLength: Float = guiText.maxLineSize,
            centered: Boolean = guiText.isCentered) {
        guiText.set(text, fontSize, font, x, y, maxLineLength, centered)
        vertex.translation.set(x, y)
        updateMesh()
    }

    init {
        guiText = GUIText("",
                1f, font, 0f, 0f, 500f)

        positionVBO = AttributeArray(6*200, textPositionAttribute)
        textureVBO = AttributeArray(6*200, VBOPointer.texCoords)
        updateMesh()

        model = Model(positionVBO, textureVBO)
        model.image = loadImage(Res.font["arial.png"])
    }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(this.model, transformation, projection, view)
    }

    private fun updateMesh() {
//        val data = meshCreator.createTextMesh(guiText)
//        positionVBO.update(data.vertices.array())
//        textureVBO.update(data.textureCoords.array())
    }
}
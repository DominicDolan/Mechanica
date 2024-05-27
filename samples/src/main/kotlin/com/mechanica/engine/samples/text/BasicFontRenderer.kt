package com.mechanica.engine.samples.text

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.shaders.models.TextModel
import com.mechanica.engine.shaders.text.Font

class BasicFontRenderer {

    private val transformation = Matrix4.identity()
    private val projection = Game.matrices.projection
    private val view = Game.matrices.uiCamera

    private val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
            """
                sample out vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                    tc = $textureCoords;
                }
                """

    }

    private val fragment = object : DrawerScript() {

        //language=GLSL
        override val main: String = """
                sample in vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                out vec4 out_Color;
                                
                void main(void) {
                    vec4 texColor = texture(samp, tc);
                    float alpha = texColor.a;
                    
                    if (alpha > 0.5) {
                        out_Color = $color;
                    } else {
                        out_Color = vec4(0.0, 0.0, 0.0, 0.0);
                    }
                }
            """

    }

    private val shader = DrawerShader.create(vertex, fragment)


    var text: String = ""
        set(value) {
            model.string = value
            field = value
        }

    private val font: Font = Font.create(Res.font["roboto/Roboto-Regular.ttf"]) {
        characterSize = 100f
        configureSDF {
            start = -20.0
            end = 20.0
        }
    }

    val model: TextModel = TextModel(text, font)

    init {
        transformation.translation.set(-Game.ui.width/2.0, Game.ui.height/2.0 - 1.0, 0.0)
    }
    fun render() {
        shader.render(model, transformation, projection, view)
    }
}
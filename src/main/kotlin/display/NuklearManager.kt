package display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWMouseButtonCallbackI
import org.lwjgl.nuklear.*
import org.lwjgl.nuklear.Nuklear.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.system.Platform
import java.nio.ByteBuffer


class NuklearManager(win: Long) {
    val ctx: NkContext = NkContext.create()

    private val ttf: ByteBuffer? = null

    private val width = 0
    private var height = 0
    private val display_width = 0
    private  var display_height = 0

    private val default_font = NkUserFont.create() // This is the Nuklear font object used for rendering text.


    private val cmds = NkBuffer.create() // Stores a list of drawing commands that will be passed to OpenGL to render the interface.

    private val null_texture = NkDrawNullTexture.create() // An empty texture used for drawing.


    /**
     * The following variables are used for OpenGL.
     */
    private var vbo = 0
    /**
     * The following variables are used for OpenGL.
     */
    private var vao: Int = 0
    /**
     * The following variables are used for OpenGL.
     */
    private var ebo: Int = 0
    private var prog = 0
    private var vert_shdr = 0
    private var frag_shdr = 0
    private var uniform_tex = 0
    private var uniform_proj = 0

    init {
        glfwSetScrollCallback(win) { window: Long, xoffset: Double, yoffset: Double ->
            stackPush().use { stack ->
                val scroll = NkVec2.mallocStack(stack)
                        .x(xoffset.toFloat())
                        .y(yoffset.toFloat())
                nk_input_scroll(ctx, scroll)
            }
        }
        glfwSetCharCallback(win) { window: Long, codepoint: Int -> nk_input_unicode(ctx, codepoint) }
        glfwSetKeyCallback(win) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            val press = action == GLFW_PRESS
            when (key) {
                GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true)
                GLFW_KEY_DELETE -> nk_input_key(ctx, NK_KEY_DEL, press)
                GLFW_KEY_ENTER -> nk_input_key(ctx, NK_KEY_ENTER, press)
                GLFW_KEY_TAB -> nk_input_key(ctx, NK_KEY_TAB, press)
                GLFW_KEY_BACKSPACE -> nk_input_key(ctx, NK_KEY_BACKSPACE, press)
                GLFW_KEY_UP -> nk_input_key(ctx, NK_KEY_UP, press)
                GLFW_KEY_DOWN -> nk_input_key(ctx, NK_KEY_DOWN, press)
                GLFW_KEY_HOME -> {
                    nk_input_key(ctx, NK_KEY_TEXT_START, press)
                    nk_input_key(ctx, NK_KEY_SCROLL_START, press)
                }
                GLFW_KEY_END -> {
                    nk_input_key(ctx, NK_KEY_TEXT_END, press)
                    nk_input_key(ctx, NK_KEY_SCROLL_END, press)
                }
                GLFW_KEY_PAGE_DOWN -> nk_input_key(ctx, NK_KEY_SCROLL_DOWN, press)
                GLFW_KEY_PAGE_UP -> nk_input_key(ctx, NK_KEY_SCROLL_UP, press)
                GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT -> nk_input_key(ctx, NK_KEY_SHIFT, press)
                GLFW_KEY_LEFT_CONTROL, GLFW_KEY_RIGHT_CONTROL -> if (press) {
                    nk_input_key(ctx, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
                } else {
                    nk_input_key(ctx, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
                    nk_input_key(ctx, NK_KEY_COPY, false)
                    nk_input_key(ctx, NK_KEY_PASTE, false)
                    nk_input_key(ctx, NK_KEY_CUT, false)
                    nk_input_key(ctx, NK_KEY_SHIFT, false)
                }
            }
        }

        glfwSetCursorPosCallback(win, GLFWCursorPosCallbackI { window: Long, xpos: Double, ypos: Double -> nk_input_motion(ctx, xpos.toInt(), ypos.toInt()) })
        glfwSetMouseButtonCallback(win, GLFWMouseButtonCallbackI { window: Long, button: Int, action: Int, mods: Int ->
            stackPush().use { stack ->
                val cx = stack.mallocDouble(1)
                val cy = stack.mallocDouble(1)
                glfwGetCursorPos(window, cx, cy)
                val x = cx[0].toInt()
                val y = cy[0].toInt()
                val nkButton: Int
                nkButton = when (button) {
                    GLFW_MOUSE_BUTTON_RIGHT -> NK_BUTTON_RIGHT
                    GLFW_MOUSE_BUTTON_MIDDLE -> NK_BUTTON_MIDDLE
                    else -> NK_BUTTON_LEFT
                }
                nk_input_button(ctx, nkButton, x, y, action == GLFW_PRESS)
            }
        })

        nk_init(ctx, ALLOCATOR, null)
        ctx.clip().copy { handle, text, len ->
            if (len == 0) {
                return@copy
            }

            stackPush().use { stack ->
                val str: ByteBuffer = stack.malloc(len + 1)
                memCopy(text, memAddress(str), len.toLong())
                str.put(len, 0.toByte())
                glfwSetClipboardString(win, str)
            }
        }.paste { handle, edit ->
            val text: Long = nglfwGetClipboardString(win)
            if (text != NULL) {
                nnk_textedit_paste(edit, text, nnk_strlen(text))
            }
        }

        setupContext()

    }

    private fun setupContext() {
        val NK_SHADER_VERSION = if (Platform.get() === Platform.MACOSX) "#version 150\n" else "#version 300 es\n"
        val vertex_shader = NK_SHADER_VERSION +
                "uniform mat4 ProjMtx;\n" +
                "in vec2 Position;\n" +
                "in vec2 TexCoord;\n" +
                "in vec4 Color;\n" +
                "out vec2 Frag_UV;\n" +
                "out vec4 Frag_Color;\n" +
                "void main() {\n" +
                "   Frag_UV = TexCoord;\n" +
                "   Frag_Color = Color;\n" +
                "   gl_Position = ProjMtx * vec4(Position.xy, 0, 1);\n" +
                "}\n"
        val fragment_shader = NK_SHADER_VERSION +
                "precision mediump float;\n" +
                "uniform sampler2D Texture;\n" +
                "in vec2 Frag_UV;\n" +
                "in vec4 Frag_Color;\n" +
                "out vec4 Out_Color;\n" +
                "void main(){\n" +
                "   Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n" +
                "}\n"

        nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE)
        prog = glCreateProgram()
        vert_shdr = glCreateShader(GL_VERTEX_SHADER)
        frag_shdr = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(vert_shdr, vertex_shader)
        glShaderSource(frag_shdr, fragment_shader)
        glCompileShader(vert_shdr)
        glCompileShader(frag_shdr)
        check(glGetShaderi(vert_shdr, GL_COMPILE_STATUS) == GL_TRUE)
        check(glGetShaderi(frag_shdr, GL_COMPILE_STATUS) == GL_TRUE)
        glAttachShader(prog, vert_shdr)
        glAttachShader(prog, frag_shdr)
        glLinkProgram(prog)
        check(glGetProgrami(prog, GL_LINK_STATUS) == GL_TRUE)

        uniform_tex = glGetUniformLocation(prog, "Texture")
        uniform_proj = glGetUniformLocation(prog, "ProjMtx")
        val attrib_pos: Int = glGetAttribLocation(prog, "Position")
        val attrib_uv: Int = glGetAttribLocation(prog, "TexCoord")
        val attrib_col: Int = glGetAttribLocation(prog, "Color")

        // buffer setup
        vbo = glGenBuffers()
        ebo = glGenBuffers()
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glEnableVertexAttribArray(attrib_pos)
        glEnableVertexAttribArray(attrib_uv)
        glEnableVertexAttribArray(attrib_col)
        glVertexAttribPointer(attrib_pos, 2, GL_FLOAT, false, 20, 0)
        glVertexAttribPointer(attrib_uv, 2, GL_FLOAT, false, 20, 8)
        glVertexAttribPointer(attrib_col, 4, GL_UNSIGNED_BYTE, true, 20, 16)


        // null texture setup
        val nullTexID: Int = glGenTextures()
        null_texture.texture().id(nullTexID)
        null_texture.uv().set(0.5f, 0.5f)
        glBindTexture(GL_TEXTURE_2D, nullTexID)
        stackPush().use { stack -> glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(-0x1)) }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)

        glBindTexture(GL_TEXTURE_2D, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    companion object {
        val ALLOCATOR: NkAllocator = NkAllocator.create()
        .alloc { _, _, size -> nmemAllocChecked(size)}
        .mfree { _, ptr -> nmemFree(ptr) }

        val VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
            .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
            .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
            .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
            .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
            .flip()

        private const val BUFFER_INITIAL_SIZE: Long = 4 * 1024
    }
}
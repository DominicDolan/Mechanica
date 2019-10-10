package graphics.framebuffer

import display.Game
import graphics.Image
import loader.loadTexturedQuad
import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glTexParameterf
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS
import org.lwjgl.opengl.GL30
import graphics.frameRenderer
import java.nio.ByteBuffer

class Fbo (private val width: Int, private val height: Int, depthBufferType: Int = DEPTH_RENDER_BUFFER) {

    private var frameBuffer: Int = 0
    private val quad: Model = loadTexturedQuad(Image(0), -1f, 1f, 1f, -1f)

    /**
     * @return The ID of the texture containing the colour buffer of the FBO.
     */
    var colourTexture: Image = Image(0)
        private set
    /**
     * @return The texture containing the FBOs depth buffer.
     */
    var depthTexture: Int = 0
        private set

    private var depthBuffer: Int = 0
    private var colourBuffer: Int = 0

    /**
     * Creates an FBO of a specified width and height, with the desired type of
     * depth buffer attachment.
     *
     * @param width
     * - the width of the FBO.
     * @param height
     * - the height of the FBO.
     * @param depthBufferType
     * - an int indicating the type of depth buffer attachment that
     * this FBO should use.
     */

    init {
        createFrameBuffer()
        createTextureAttachment();
        //		if (type == DEPTH_RENDER_BUFFER) {
//        			createDepthBufferAttachment();
//        createMultisampledColorAttachment()
        //		} else if (type == DEPTH_TEXTURE) {
//        			createDepthTextureAttachment();
        //		}
        unbindFrameBuffer()
    }

    /**
     * Creates a new frame buffer object and sets the buffer to which drawing
     * will occur - colour attachment 0. This is the attachment where the colour
     * buffer texture is.
     *
     */
    private fun createFrameBuffer() {
        frameBuffer = GL30.glGenFramebuffers()
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer)
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0)
    }


    /**
     * Creates a texture and sets it as the colour buffer attachment for this
     * FBO.
     */
    private fun createTextureAttachment() {
        colourTexture = Image(GL11.glGenTextures())
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture.id)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                null as ByteBuffer?)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -1.0f)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
//        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
//            val amount = Math.min(4f, glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT))
//            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, amount)
//        }

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture.id, 0)
    }

    /**
     * Binds the frame buffer, setting it as the current render target. Anything
     * rendered after this will be rendered to this FBO, and not to the screen.
     */
    fun bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer)
        GL11.glViewport(0, 0, width, height)
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target. Anything rendered after this will be rendered to the
     * screen, and not this FBO.
     */
    fun unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GL11.glViewport(0, 0, Game.width, Game.height)
    }

    fun doPostProcessing(){
        quad.texture = colourTexture
        frameRenderer(quad)
    }

    /**
     * Binds the current FBO to be read from (not used in tutorial 43).
     */
    fun bindToRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer)
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0)
    }

    fun resolveToScreen() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0)
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer)
        GL11.glDrawBuffer(GL11.GL_BACK)
        GL30.glBlitFramebuffer(
                0, 0, width, height,
                0, 0,  Game.width, Game.height,
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST)
        this.unbindFrameBuffer()
    }


    /**
     * Adds a depth buffer to the FBO in the form of a texture, which can later
     * be sampled.
     */
    private fun createDepthTextureAttachment() {
        depthTexture = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0, GL11.GL_DEPTH_COMPONENT,
                GL11.GL_FLOAT, null as ByteBuffer?)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0)
    }

    private fun createMultisampledColorAttachment() {
        colourBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffer)
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 8, GL11.GL_RGBA8, width, height)
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RENDERBUFFER, colourBuffer)
    }

    /**
     * Adds a depth buffer to the FBO in the form of a render buffer. This can't
     * be used for sampling in the renderers.
     */
    private fun createDepthBufferAttachment() {
        depthBuffer = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer)
        GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 8, GL14.GL_DEPTH_COMPONENT24, width, height)
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer)
    }


    /**
     * Deletes the frame buffer and its attachments when the game closes.
     */
    fun cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer)
        GL11.glDeleteTextures(colourTexture.id)
        GL11.glDeleteTextures(depthTexture)
        GL30.glDeleteRenderbuffers(depthBuffer)
        GL30.glDeleteRenderbuffers(colourBuffer)
    }

    companion object {

        val NONE = 0
        val DEPTH_TEXTURE = 1
        val DEPTH_RENDER_BUFFER = 2
    }

}


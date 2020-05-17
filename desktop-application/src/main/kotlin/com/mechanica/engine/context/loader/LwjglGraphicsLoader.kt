package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Image
import com.mechanica.engine.models.Model
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.shader.LwjglImage
import org.lwjgl.opengl.GL11

class LwjglGraphicsLoader : GraphicsLoader {

    override fun loadImage(id: Int) = LwjglImage(id)
    override fun loadImage(res: Resource) = com.mechanica.engine.utils.loadImage(res)

    override fun drawArrays(model: Model) {
        if (model.vertexCount > 0) {
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.vertexCount)
        }
    }

    override fun drawElements(model: Model) {
        if (model.vertexCount > 0) {
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
    }
}
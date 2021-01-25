package com.mechanica.engine.shaders.draw

import com.mechanica.engine.shaders.models.Model


interface ArrayDrawCommand {
    fun arrays(model: Model) = arrays(model.vertexCount)
    fun arrays(vertexCount: Int)
}

interface ElementsDrawCommand {
    fun elements(model: Model) = elements(model.vertexCount)
    fun elements(vertexCount: Int)
}

interface MultiArrayDrawCommand {
    fun arrays(models: Array<Model>)
}

interface MultiElementsDrawCommand {
    fun elements(models: Array<Model>)
}

interface DrawCommands : ArrayDrawCommand, ElementsDrawCommand {
    fun model(model: Model) {
        if (model.hasIndexArray) {
            elements(model)
        } else {
            arrays(model)
        }
    }
}

interface MultiDrawCommands : DrawCommands, MultiArrayDrawCommand, MultiElementsDrawCommand
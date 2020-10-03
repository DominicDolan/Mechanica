package com.mechanica.engine.matrix

import org.joml.Matrix4f

interface Matrices {
    val projection: Matrix4f
    val worldCamera: Matrix4f
    val uiCamera: Matrix4f

}
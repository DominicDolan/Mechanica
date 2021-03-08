package com.mechanica.engine.util

import com.cave.library.matrix.mat4.Matrix4

interface CameraMatrices {
    val projection: Matrix4
    val worldCamera: Matrix4
    val uiCamera: Matrix4
}
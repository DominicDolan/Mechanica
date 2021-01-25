package com.mechanica.engine.resources

import com.mechanica.engine.shaders.models.Image

fun Image.Companion.create(resource: Resource) = create(resource.buffer)
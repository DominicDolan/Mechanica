package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.AttributeVars
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.vars.GlslLocation


interface AttributeLoader {
    fun createLocationLoader(locationName: String): GlslLocation
    fun variables(qualifier: AttributeQualifier) : AttributeVars

    fun disableAttributeArray(value: Int)
}
package com.mechanica.engine.shader.vars.attributes

import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.vars.ShaderVariable
import com.mechanica.engine.shader.vars.attributes.vars.AttributeType

interface AttributeVariable : ShaderVariable {
    override val qualifier: AttributeQualifier
    override val type: AttributeType
}
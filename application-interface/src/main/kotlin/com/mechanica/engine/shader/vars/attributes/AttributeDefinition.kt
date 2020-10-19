package com.mechanica.engine.shader.vars.attributes

import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.vars.ShaderVariableDefinition
import com.mechanica.engine.shader.vars.attributes.vars.AttributeType

interface AttributeDefinition : ShaderVariableDefinition {
    override val qualifier: AttributeQualifier
    override val type: AttributeType
}
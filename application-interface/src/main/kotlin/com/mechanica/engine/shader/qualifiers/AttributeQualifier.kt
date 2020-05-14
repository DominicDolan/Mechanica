package com.mechanica.engine.shader.qualifiers

interface AttributeQualifier : Qualifier {
    val location: Int
    override val qualifierName: String
        get() = "layout (location=$location) in"

    companion object {
        fun get(location: Int) = object : AttributeQualifier {
            override val location = location
            override fun toString() = qualifierName
        }
    }
}
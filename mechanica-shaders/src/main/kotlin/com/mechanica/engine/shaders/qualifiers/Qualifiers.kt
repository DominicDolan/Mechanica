package com.mechanica.engine.shaders.qualifiers

interface Qualifier {
    val qualifierName: String

    override fun toString(): String
}

interface AttributeQualifier : Qualifier {
    override val qualifierName: String
        get() = "in"
}

interface UniformQualifier : Qualifier {
    override val qualifierName: String
        get() = "uniform"
}

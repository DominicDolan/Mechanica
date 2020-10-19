package com.mechanica.engine.shader.qualifiers

interface Qualifier {
    val qualifierName: String

    override fun toString(): String
}

interface AttributeQualifier : Qualifier {
    val location: Int
    override val qualifierName: String
        get() = "layout (location=$location) in"
}

//class AttributeArrayBuilder(private val variables: ScriptVariables? = null) {
//    var name: String? = null
//
//    var qualifier: AttributeQualifier? = null
//    var location: Int = 0
//
//    var coordinateSize = 3
//    var type: AttributeType? = null
//
//    constructor(qualifier: AttributeQualifier) {
//        this.qualifier = qualifier
//    }
//
//    fun build(size: Int, setter: AttributeArray.() -> Unit): AttributeArray {
//        val name = this.name ?: variables?.getNextName() ?: ""
////        val qualifier = this.qualifier ?: AttributeQualifier.create(location)
//        val type = this.type ?: AttributeType.create(coordinateSize)
//        val loader = GLLoader.createAttributeLoader(qualifier)
//
//        val variable = object : AttributeVariable {
//            override val qualifier: AttributeQualifier = qualifier
//            override val type: AttributeType = type
//            override val name: String = name
//            override fun toString() = name
//        }
//
//        val v = loader.createAttributeArray(size, variable)
//
//        setter(v)
//        return v
//    }
//
//
//}
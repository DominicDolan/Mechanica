package com.mechanica.engine.persistence

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PersistentVariable<T:Any>(
        private val map: PersistenceMap,
        private val defaultValue: T,
        private val instance: String? = null) : ReadWriteProperty<Any,T> {

    private var hasRetrieved = false
    private var value: T = defaultValue

    private var fullPropertyName: String? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (!hasRetrieved) {
            val name = fullPropertyName(thisRef, property)
            value = if (instance == null) {
                map.retrieveSingleValue(name)
            } else {
                map.retrieveInstancedValue(instance, name)
            }
            hasRetrieved = true
        }
        return value
    }

    private fun Map<*, *>.retrieveSingleValue(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return this[name] as? T ?: defaultValue
    }

    private fun Map<*, *>.retrieveInstancedValue(instance: String, name: String): T {
        val subMap = subMap(name) ?: return defaultValue

        return subMap.retrieveSingleValue(instance)
    }

    private fun Map<*, *>.subMap(name: String) : HashMap<String, Any>? {
        @Suppress("UNCHECKED_CAST")
        return this[name] as? HashMap<String, Any>
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
        val name = fullPropertyName(thisRef, property)
        if (instance == null) {
            map.put(name, value)
        } else {
            val subMap = map.subMap(name)
            if (subMap == null) {
                val newMap = HashMap<String, Any>()
                newMap[instance] = value
                map.put(name, newMap)
            } else {
                subMap[instance] = value
            }
        }
    }

    private fun fullPropertyName(ref: Any, property: KProperty<*>): String {
        val fullName = this.fullPropertyName
        return if (fullName != null) fullName
        else {
            val newName = ref::class.java.canonicalName + "." + property.name
            fullPropertyName = newName
            newName
        }
    }
}
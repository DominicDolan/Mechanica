package com.mechanica.engine.persistence

import com.mechanica.engine.util.getCallingClass

private val path = getCallingClass().packageName + "/res/data/persistence.json"
private val map = PersistenceMap(path)

fun persistent(default: Boolean, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: Int, instance: String? = null) = PersistentVariable(map, default, instance)
fun persistent(default: Long, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: Float, instance: String? = null) = PersistentVariable(map, default, instance)
fun persistent(default: Double, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: String, instance: String? = null) = PersistentVariable(map, default, instance)

fun storeData() {
    map.store()
}

fun populateData() {
    map.populate()
}

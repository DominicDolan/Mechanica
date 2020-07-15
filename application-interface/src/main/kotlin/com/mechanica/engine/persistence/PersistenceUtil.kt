package com.mechanica.engine.persistence

private val map = PersistenceMap("res/data/persistence.json")

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

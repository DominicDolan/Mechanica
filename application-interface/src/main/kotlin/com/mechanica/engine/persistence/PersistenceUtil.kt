package com.mechanica.engine.persistence

private val map = PersistenceMap("res/data/persistence.txt")

fun persist(default: Boolean, instance: String? = null) = PersistentVariable(map, default, instance)

fun persist(default: Int, instance: String? = null) = PersistentVariable(map, default, instance)
fun persist(default: Long, instance: String? = null) = PersistentVariable(map, default, instance)

fun persist(default: Float, instance: String? = null) = PersistentVariable(map, default, instance)
fun persist(default: Double, instance: String? = null) = PersistentVariable(map, default, instance)

fun persist(default: String, instance: String? = null) = PersistentVariable(map, default, instance)

fun storeData() {
    map.store()
}

fun populateData() {
    map.populate()
}

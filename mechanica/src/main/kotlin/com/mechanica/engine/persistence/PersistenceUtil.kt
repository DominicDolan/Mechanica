package com.mechanica.engine.persistence

import com.mechanica.engine.game.Game

private val map: PersistenceMap
    get() = Game.persistenceMap ?: throw UninitializedPropertyAccessException("Persistence has not yet been initialized for this context. Game.configure() should be called first or setPersistence() should be called during configuration")

fun persistent(default: Boolean, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: Int, instance: String? = null) = PersistentVariable(map, default, instance)
fun persistent(default: Long, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: Float, instance: String? = null) = PersistentVariable(map, default, instance)
fun persistent(default: Double, instance: String? = null) = PersistentVariable(map, default, instance)

fun persistent(default: String, instance: String? = null) = PersistentVariable(map, default, instance)

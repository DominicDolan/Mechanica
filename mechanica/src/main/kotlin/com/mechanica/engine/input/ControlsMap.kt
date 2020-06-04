package com.mechanica.engine.input

abstract class ControlsMap {
    private val map = HashMap<Int, ArrayList<Key>>()

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(keyId))
    }

    protected fun mapToKeys(vararg keys: Keys): Key {
        return Key(*keys)
    }

    companion object {
        fun getKeyEnum(keyId: Int): Keys? {
            for (value in Keys.values()) {
                if (keyId == value.id)
                    return value
            }
            return null
        }
    }
}
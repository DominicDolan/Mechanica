package com.mechanica.engine.input

internal class PressedKeys(private val map: HashMap<Int, ArrayList<Key>>) : Iterable<Key>, Iterator<Key> {
    private val emptyElement = -10
    private var pressedKeys = IntArray(100) { emptyElement }
    private var current = 0
    private var currentIndex = -1
    var size = 0; private set

    override fun hasNext(): Boolean {
        val hasNext = current < size
        if (!hasNext) {
            current = 0
            currentIndex = -1
        }
        return hasNext
    }

    override fun next(): Key {
        current++
        var nextIndex = currentIndex + 1
        while (pressedKeys[nextIndex] == -10) {
            nextIndex++
        }
        currentIndex = nextIndex

        val keyId = pressedKeys[currentIndex]
        val keys = map[keyId]
        if (keys != null && keys.size > 0) {
            return keys.first()
        }
        return Key(map, keyId)
    }

    override fun iterator() = this

    fun add(key: Int) {
        if (!pressedKeys.contains(key)) {
            var firstAvailable = 0
            while (pressedKeys[firstAvailable] != -10) {
                firstAvailable++
            }
            sizeCheck()
            pressedKeys[firstAvailable] = key
            size++
        }
    }

    fun remove(key: Int) {
        val index = pressedKeys.indexOf(key)
        if (index != -1) {
            pressedKeys[index] = emptyElement
            if (size > 0) {
                size--
            }
        }
    }

    private fun sizeCheck() {
        if (size + 1 >= pressedKeys.size) {
            pressedKeys = IntArray(pressedKeys.size*2) {
                if (pressedKeys.size < it) {
                    pressedKeys[it]
                } else emptyElement
            }
        }
    }
}
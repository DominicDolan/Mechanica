package com.mechanica.engine.memory

class SimulatedStack<T>(private val initializer: () -> T) {

    private val memoryList = ArrayList<T>()
    private var allocatedMemory = BooleanArray(100) { false }


    fun allocate(): T {
        if (allocatedMemory.size <= memoryList.size) resize()

        var firstFree: Int = -1
        for (i in memoryList.indices) {
            if (!allocatedMemory[i]) {
                allocatedMemory[i] = true
                firstFree = i
                break
            }
        }

        return if (firstFree == -1) {
            val new = initializer()
            memoryList.add(new)
            new
        } else {
            memoryList[firstFree]
        }

    }

    fun freeMemory(index: Int) {
        allocatedMemory[index] = false
    }

    fun freeMemory(item: T) {
        val index = memoryList.indexOf(item)
        if (index != -1)
            freeMemory(index)
    }

    inline fun use(block: (T) -> Unit) {
        val item = allocate()
        block(item)
        freeMemory(item)
    }


    private fun resize() {
        val newArray = BooleanArray(allocatedMemory.size*2) {
            if (it < allocatedMemory.size) allocatedMemory[it]
            else false
        }

        allocatedMemory = newArray
    }
}
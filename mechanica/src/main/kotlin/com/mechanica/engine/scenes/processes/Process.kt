package com.mechanica.engine.scenes.processes

import kotlin.math.max

abstract class Process(override val priority: Int = 0) : ProcessNode {

    protected val childProcesses: List<ProcessNode> = ArrayList()

    final override fun <P:ProcessNode> addProcess(process: P): P {
        val processes = (childProcesses as ArrayList)

        processes.add(process)
        processes.sortBy { it.priority }
        return process
    }

    final override fun removeProcess(process: ProcessNode): Boolean {
        process.destructNodes()
        return (childProcesses as ArrayList).remove(process)
    }

    final override fun <P:ProcessNode> replaceProcess(old: P, new: P): P {
        val processes = (childProcesses as ArrayList)
        val index = processes.indexOf(old)
        if (index != -1) {
            removeProcess(processes[index])
            processes.add(index, new)
            processes.sortBy { it.priority }
            return new
        }
        return old
    }

    inline fun forEachProcess(operation: (ProcessNode)-> Unit) {
        for (i in `access$childProcesses`.indices) {
            operation(`access$childProcesses`[i])
        }
    }

    override fun updateNodes(delta: Double) {
        val index = updateNodesFor(delta) { it.priority < 0 }
        this.update(delta)
        updateNodesFor(delta, index) { it.priority >= 0 }
    }

    private inline fun updateNodesFor(delta: Double, from: Int = 0, condition: (ProcessNode) -> Boolean): Int {
        var i = from
        do {
            val process = childProcesses.getOrNull(i++) ?: break
            process.updateNodes(delta)
        } while (condition(process))
        return i
    }

    override fun destructNodes() {
        for (i in childProcesses.indices) {
            childProcesses[i].destructNodes()
        }
        destructor()
    }

    override fun destructor() { }

    protected fun finalize() { destructor() }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childProcesses`: List<ProcessNode>
        get() = childProcesses
}

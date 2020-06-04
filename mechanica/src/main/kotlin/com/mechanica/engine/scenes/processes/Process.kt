package com.mechanica.engine.scenes.processes

abstract class Process : ProcessNode {

    protected val childProcesses: List<ProcessNode> = ArrayList()

    override fun <P:ProcessNode> addProcess(process: P): P {
        (childProcesses as ArrayList).add(process)
        return process
    }

    override fun removeProcess(process: ProcessNode): Boolean {
        process.destructor()
        return (childProcesses as ArrayList).remove(process)
    }

    override fun <P:ProcessNode> replaceProcess(old: P, new: P): P {
        val index = childProcesses.indexOf(old)
        if (index != -1) {
            childProcesses[index].destructor()
            (childProcesses as ArrayList)[index] = new
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
        this.update(delta)
        for (i in childProcesses.indices) {
            childProcesses[i].updateNodes(delta)
        }
    }

    override fun destructor() { }

    protected fun finalize() { destructor() }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childProcesses`: List<ProcessNode>
        get() = childProcesses
}

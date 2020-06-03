package com.mechanica.engine.scenes.processes

abstract class Process : ProcessNode {

    protected val childProcesses: List<Process> = ArrayList()

    override fun <P:Process> addProcess(process: P): P {
        (childProcesses as ArrayList).add(process)
        return process
    }

    override fun removeProcess(process: Process): Boolean {
        process.destructor()
        return (childProcesses as ArrayList).remove(process)
    }

    override fun <P:Process> replaceProcess(old: P, new: P): P {
        val index = childProcesses.indexOf(old)
        if (index != -1) {
            childProcesses[index].destructor()
            (childProcesses as ArrayList)[index] = new
            return new
        }
        return old
    }

    inline fun forEachProcess(operation: (Process)-> Unit) {
        for (i in `access$childProcesses`.indices) {
            operation(`access$childProcesses`[i])
        }
    }

    internal open fun internalUpdate(delta: Double) {
        this.update(delta)
        for (i in childProcesses.indices) {
            childProcesses[i].internalUpdate(delta)
        }
    }

    override fun destructor() { }

    protected fun finalize() { destructor() }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childProcesses`: List<Process>
        get() = childProcesses
}

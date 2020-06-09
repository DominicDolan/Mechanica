package com.mechanica.engine.scenes.processes

import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap

abstract class Process(override val priority: Int = 0) : ProcessNode {

    private val activationListeners = ArrayList<(Boolean) -> Unit>()
    final override var active: Boolean = true
        set(value) {
            val hasChanged = field != value
            field = value
            if (hasChanged) {
                runActivationCallbacks(value)
            }
        }

    private val childProcesses: List<ProcessNode> = ArrayList()

    fun addActivationChangedListener(listener: (Boolean) -> Unit) {
        activationListeners.add(listener)
        runActivationCallbacks(active)
    }

    private fun runActivationCallbacks(activate: Boolean) {
        for (i in activationListeners.indices) {
            activationListeners[i].invoke(activate)
        }
    }

    final override fun <P:ProcessNode> addProcess(process: P): P {
        val processes = (childProcesses as ArrayList)

        if (!processes.contains(process)) {
            processes.add(process)
            processes.sortBy { it.priority }
        }
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
            if (process.active) {
                process.updateNodes(delta)
            }
        } while (condition(process))
        return i
    }

    override fun destructNodes() {
        for (i in childProcesses.indices) {
            childProcesses[i].destructNodes()
        }
        destructor()
    }

    /**
     * Sets the [processes] in which only one process can be active at once. This exclusivity is automatically enforced
     *
     * Usage:
     * ```
     * val currentlyActiveProcess by exclusivelyActiveProcesses(Process1(), Process2(), Process3())
     * ```
     *
     * @param processes the set of processes that will be added as children to this process and only one can be
     *                  set to active at a time
     * @return An ExclusiveProcessMap which can have more processes added at a later stage
     */
    protected fun <P: Process> exclusivelyActiveProcesses(vararg processes: P): ExclusiveActivationMap<P> {
        return ExclusiveProcessMap(*processes)
    }

    private inner class ExclusiveProcessMap<P: Process>(vararg processes: P) : ExclusiveActivationMap<P>(*processes) {
        override fun <R : P> addProcess(process: R): R {
            this@Process.addProcess(process)
            return super.addProcess(process)
        }
    }

    override fun destructor() { }

    protected fun finalize() { destructor() }

    @Suppress("PropertyName")
    @PublishedApi
    internal val `access$childProcesses`: List<ProcessNode>
        get() = childProcesses
}

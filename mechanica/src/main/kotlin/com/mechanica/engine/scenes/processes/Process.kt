package com.mechanica.engine.scenes.processes

import com.mechanica.engine.scenes.exclusiveScenes.ExclusiveActivationMap

abstract class Process(override val order: Int = 0) : ProcessNode {

    private val activationListeners = ArrayList<ActivationChangedEvents>()
    private var _active = true
    final override var active: Boolean
        get() = _active
        set(value) {
            val hasChanged = _active != value
            if (hasChanged) {
                runActivationCallbacks(value)
            }
        }

    private val childProcesses: List<ProcessNode> = ArrayList()

    private var callbacksInitialized = false

    init {
        addActivationChangedListener(0) { _active = it }
    }

    /**
     * Adds a callback for when the value of [active] is changed. The [priority] value dictates the order in which
     * the callbacks are executed, the higher the priority value, the earlier the callback will execute.
     * The value can be negative, in which case the callback will be called after the value for [active]
     * has been set
     *
     * @param priority an integer value which represents the order that the callbacks are executed, the higher
     * the value, the earlier the execution, the value for [active] is changed at a priority of zero
     *
     * @param listener the callback which will execute when the value of [active] has been changed, the lambda takes
     * a boolean expression which is the new value for [active]
     */
    fun addActivationChangedListener(priority: Int = 0, listener: (Boolean) -> Unit) {
        activationListeners.add(ActivationChangedEvents(priority, listener))
        activationListeners.sortByDescending { it.priority }
    }

    private fun runActivationCallbacks(activate: Boolean) {
        callbacksInitialized = true
        for (i in activationListeners.indices) {
            activationListeners[i].listener.invoke(activate)
        }
    }

    final override fun <P:ProcessNode> addProcess(process: P): P {
        val processes = (childProcesses as ArrayList)

        if (!processes.contains(process)) {
            processes.add(process)
            processes.sortBy { it.order }
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
            processes.sortBy { it.order }
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
        if (!callbacksInitialized) runActivationCallbacks(active)

        val index = updateNodesFor(delta) { it.order < 0 }
        this.update(delta)
        updateNodesFor(delta, index) { it.order >= 0 }
    }

    /**
     * A function to be overridden which will run while [active] is set to false
     */
    open fun whileInactive(delta: Double) { }

    private inline fun updateNodesFor(delta: Double, from: Int = 0, condition: (ProcessNode) -> Boolean): Int {
        var i = from
        do {
            val process = childProcesses.getOrNull(i++) ?: break
            if (process.active) {
                process.updateNodes(delta)
            } else if (process is Process) {
                process.whileInactive(delta)
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

    private class ActivationChangedEvents(val priority: Int, val listener: (Boolean) -> Unit)
}

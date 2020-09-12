package com.mechanica.engine.scenes.processes

import com.mechanica.engine.util.extensions.fori

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
    override var paused: Boolean = false
    private val childProcesses: List<ProcessNode> = ArrayList()
    private val leafProcesses: List<Updateable> = ArrayList()

    private var callbacksInitialized = false

    init {
        addActivationChangedListener(0) {
            _active = it
            if (it) activated()
            else deactivated()
        }
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

    final override fun <P:Updateable> addProcess(process: P): P {
        if (process is ProcessNode) {
            addProcessNode(process)
        } else {
            addUpdateable(process)
        }
        return process
    }

    private fun addProcessNode(process: ProcessNode) {
        val processes = (childProcesses as ArrayList)

        if (!processes.contains(process)) {
            processes.add(process)
            processes.sortBy { it.order }
        }
    }

    private fun addUpdateable(updateable: Updateable) {
        val processes = (leafProcesses as ArrayList)

        if (!processes.contains(updateable)) {
            processes.add(updateable)
        }
    }

    final override fun removeProcess(process: Updateable): Boolean {
        return if (process is ProcessNode) {
            removeProcessNode(process)
        } else {
            removeUpdateable(process)
        }
    }

    private fun removeProcessNode(process: ProcessNode): Boolean {
        process.destructNodes()
        return (childProcesses as ArrayList).remove(process)
    }

    private fun removeUpdateable(updateable: Updateable): Boolean {
        return (leafProcesses as ArrayList).remove(updateable)
    }

    final override fun <P : Updateable> replaceProcess(old: P, new: P): P {
        val success = if (old is ProcessNode && new is ProcessNode) {
            replaceProcessNode(old, new)
        } else {
            replaceUpdateable(old, new)
        }

        return if (success) new else old
    }

    private fun replaceProcessNode(old: ProcessNode, new: ProcessNode): Boolean {
        val processes = (childProcesses as ArrayList)
        val index = processes.indexOf(old)
        if (index != -1) {
            removeProcess(processes[index])
            processes.add(index, new)
            processes.sortBy { it.order }
            return true
        }
        return false
    }

    private fun replaceUpdateable(old: Updateable, new: Updateable): Boolean {
        val processes = (leafProcesses as ArrayList)
        val index = processes.indexOf(old)
        if (index != -1) {
            removeProcess(processes[index])
            processes.add(index, new)
            return true
        }
        return false
    }

    inline fun forEachProcess(operation: (ProcessNode)-> Unit) {
        for (i in `access$childProcesses`.indices) {
            operation(`access$childProcesses`[i])
        }
    }

    override fun updateNodes(delta: Double) {
        if (!callbacksInitialized) runActivationCallbacks(active)

        val index = updateNodesFor(delta) { it.order < 0 }
        updateLeaves(delta)
        this.update(delta)
        updateNodesFor(delta, index) { it.order >= 0 }
    }

    override fun update(delta: Double) { }

    /**
     * A function to be overridden which will run while [active] is set to false
     */
    open fun whileInactive(delta: Double) { }

    /**
     * Function to be overriden that will be called immediately after [active] has been set to true
     */
    open fun activated() { }

    /**
     * Function to be overriden that will be called immediately after [active] has been set to false
     */
    open fun deactivated() { }

    private inline fun updateNodesFor(delta: Double, from: Int = 0, condition: (ProcessNode) -> Boolean): Int {
        var inverseI = childProcesses.lastIndex - from
        do {
            val process = childProcesses.getOrNull(childProcesses.lastIndex - (inverseI--)) ?: break
            if (process.active && !process.paused) {
                process.updateNodes(delta)
            } else if (process is Process) {
                process.whileInactive(delta)
            }
        } while (condition(process))
        return childProcesses.lastIndex - inverseI
    }

    private fun updateLeaves(delta: Double) {
        leafProcesses.fori {
            it.update(delta)
        }
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

    private class ActivationChangedEvents(val priority: Int, val listener: (Boolean) -> Unit)
}

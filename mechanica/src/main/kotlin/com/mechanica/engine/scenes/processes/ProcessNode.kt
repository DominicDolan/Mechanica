package com.mechanica.engine.scenes.processes

interface ProcessNode : Updateable {
    /**
     * The [order] value dictates the order in which the processes are updated and/or rendered,
     * the lower the order value, the earlier the process will update.
     * The value can be negative, in which case the process will be updated before the parent process
     * has been updated. If it is positive, it will happen after.
     */
    val order: Int
        get() = 0
    val active: Boolean
        get() = true
    fun <P:Updateable> addProcess(process: P): P
    fun removeProcess(process: Updateable): Boolean
    fun <P:Updateable> replaceProcess(old: P, new: P): P
    fun updateNodes(delta: Double)
    fun destructor()
    fun destructNodes()
}
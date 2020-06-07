package com.mechanica.engine.scenes.processes

interface ProcessNode : Updateable {
    val priority: Int
        get() = 0
    fun <P:ProcessNode> addProcess(process: P): P
    fun removeProcess(process: ProcessNode): Boolean
    fun <P:ProcessNode> replaceProcess(old: P, new: P): P
    fun updateNodes(delta: Double)
    fun destructor()
    fun destructNodes()
}
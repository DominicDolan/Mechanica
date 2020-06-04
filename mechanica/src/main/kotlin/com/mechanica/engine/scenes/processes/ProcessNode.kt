package com.mechanica.engine.scenes.processes

interface ProcessNode : Updateable {
    fun <P:ProcessNode> addProcess(process: P): P
    fun removeProcess(process: ProcessNode): Boolean
    fun <P:ProcessNode> replaceProcess(old: P, new: P): P
    fun updateNodes(delta: Double)
    fun destructor()
}
package com.mechanica.engine.scenes.processes

interface ProcessNode : Updateable {
    fun <P:Process> addProcess(process: P): P
    fun removeProcess(process: Process): Boolean
    fun <P:Process> replaceProcess(old: P, new: P): P
    fun destructor()
}
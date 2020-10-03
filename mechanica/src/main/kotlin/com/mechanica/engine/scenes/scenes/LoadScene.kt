package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.scenes.processes.LoadProcess

abstract class LoadScene(order: Int = 0, waitTime: Float = 0.2f, waitLoops: Int = 2) : Scene(order) {

    init {
        addProcess(object : LoadProcess(order = -1, waitTime, waitLoops) {
            override fun load() {
                this@LoadScene.load()
            }

            override fun onFinish() {
                this@LoadScene.onFinish()
            }
        })
    }

    abstract fun load()

    abstract fun onFinish()
}
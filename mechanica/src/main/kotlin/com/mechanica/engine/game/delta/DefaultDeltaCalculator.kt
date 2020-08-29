package com.mechanica.engine.game.delta

internal class DefaultDeltaCalculator : DeltaCalculator {
    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        update(thisFrame - lastFrame)
        render()
    }


//    var accumulator = 0.0
//    val dt = 1.0/300.0
//    private fun updateScene() {
//        val now = Timer.now
//        updateDuration = now - startOfLoop
//        startOfLoop = Timer.now
//
//        accumulator += updateDuration
//
////        println("update scene")
//        var count = 0
//        while (accumulator >= dt) {
//            count++
//            println(count)
////            println("update nodes")
//            updateNodes(dt)
//            accumulator -= dt
//        }
//
//        println(accumulator)
//    }


}
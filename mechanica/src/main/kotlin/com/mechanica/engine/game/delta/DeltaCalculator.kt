package com.mechanica.engine.game.delta

interface DeltaCalculator {

    fun updateAndRender(lastFrame: Double, thisFrame: Double, updater: Updater) {
        updater.updateAndRender(lastFrame, thisFrame)
    }

    fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double)

    companion object {
        /**
         *  Simply calculates the difference between this frame and last frame and passes it into update(), calling it only once per frame,
         *  and then render()
         */
        fun basicVariableCalculator(): DeltaCalculator = BasicVariableCalculator()

        /**
         * Passes a value into update based on the value of [frameRate] and passes in the same value for every frame
         *
         * @param frameRate the value used to calculate the value of delta
         *
         */
        fun basicConstantCalculator(frameRate: Double = 60.0): DeltaCalculator = BasicConstantCalculator(frameRate)

        /**
         * Update() can be called multiple times for one render call and the value of [updateTime] will be passed in each
         * time. The number of times that update() is called is such that the total simulated time will add up to real time
         *
         * @param updateTime the time in seconds to pass to update()
         */
        fun multiUpdateCalculator(updateTime: Double = 1.0/120.0): DeltaCalculator = MultiUpdateCalculator(updateTime)

        /**
         * Works similarly to [multiUpdateCalculator] however it is possible that the value passed in to update()
         * might change from the value of [updateTimeApprox]
         * if the given value is not suitable, it is likely to not change often.
         *
         * @param frameTimeApprox The approximate/estimated time to do one frame loop, the default is 1.0/60.0
         * @param updateTimeApprox The approximate time to pass into update(), This refers to the simulated time not the real
         * time that it would take to update. The default is 1.0/120.0, meaning that update() should be called twice if the frame time
         * is 1.0/60.0
         */
        fun adaptiveCalculator(frameTimeApprox: Double = 1.0/60.0, updateTimeApprox: Double = 1.0/120.0): DeltaCalculator {
            return AdaptiveDeltaCalculator(frameTimeApprox, updateTimeApprox)
        }
    }
}
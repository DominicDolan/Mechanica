package com.mechanica.engine.game.delta

interface DeltaCalculator {

    /**
     * The size of one simulated step for this calculator, in seconds.
     *
     * This is what the loop passes to [step] when the game is being advanced one frame at a
     * time, so that every step is the same size no matter how long the debugger sat between them.
     */
    val timeStep: Double
        get() = 1.0/60.0

    fun updateAndRender(lastFrame: Double, thisFrame: Double, updater: Updater) {
        updater.updateAndRender(lastFrame, thisFrame)
    }

    fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double)

    /**
     * Simulates exactly one step of [delta] seconds and renders it, ignoring real time entirely.
     *
     * Used to advance a paused game frame by frame. Implementations that carry state between
     * frames should leave it consistent with the step having just happened.
     */
    fun step(updater: Updater, delta: Double) {
        updater.update(delta)
        updater.render()
    }

    /**
     * Called when real time has jumped by more than the loop is willing to simulate, which
     * means the process was suspended rather than running slowly: a breakpoint, a long GC
     * pause, an OS sleep or a window drag.
     *
     * That time never happened as far as the game is concerned, so any accumulated backlog or
     * timing statistics built from it must be discarded rather than caught up on.
     */
    fun resync() { }

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
         * might change from the value of [frameTimeApprox] if the given value is not suitable.
         * It is likely to not change often.
         *
         * @param frameTimeApprox The approximate/estimated time to do one frame loop, the default is 1.0/60.0
         */
        fun adaptiveCalculator(frameTimeApprox: Double = 1.0/60.0): DeltaCalculator {
            return AdaptiveDeltaCalculator(frameTimeApprox)
        }
    }
}
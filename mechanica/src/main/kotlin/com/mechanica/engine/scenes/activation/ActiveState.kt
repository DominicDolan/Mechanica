package com.mechanica.engine.scenes.activation

interface ActiveState : Activatable {

    val activator: ActivationListener
    override var active: Boolean
        get() = activator.getValue(this, this::active)
        set(value) = activator.setValue(this, this::active, value)


    /**
     * Function to be overriden that will be called immediately after [active] has been set to true
     */
    fun onActivate()

    /**
     * Function to be overriden that will be called immediately after [active] has been set to false
     */
    fun onDeactivate()

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
    fun addActiveStateChangedListener(priority: Int = 0, listener: (Boolean) -> Unit) {
        activator.addListener(priority, listener)
    }

    companion object {
        fun createDefault(): ActiveState {
            return object : ActiveState {
                override val activator = ActivationListener()
                override fun onActivate() {}
                override fun onDeactivate() {}
            }
        }
    }
}
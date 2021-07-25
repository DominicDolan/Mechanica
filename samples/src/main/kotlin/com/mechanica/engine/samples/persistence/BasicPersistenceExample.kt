@file:Suppress("UNUSED_CHANGED_VALUE", "UNUSED_VALUE")

package com.mechanica.engine.samples.persistence

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.persistence.persistent


fun main() {
    Game.configure {
        setFullscreen(false)
    }

    var someStringVariable by persistent("Some String")

    // This will print "Some Other String" the second time this code is run
    println(someStringVariable)

    someStringVariable = "Some Other String"

    // For variables that can have multiple instances, an instance id can be passed in
    val class1 = SomeClass("class1", "1")
    val class2 = SomeClass("class2", "2")

    class1.setValue("assigning class1 again")

    println(class1)
    println(class2)

    Game.terminate()
}

class SomeClass(valueToPersist: String, instanceId: String) {
    // The instance id is required here because there will be multiple instances of SomeClass
    private var variable by persistent(valueToPersist, instance = instanceId)

    fun setValue(string: String) {
        variable = string
    }

    override fun toString() = variable
}
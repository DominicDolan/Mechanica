@file:Suppress("unused") // There will be many functions here that go unused most of the time
package units

import game.Game


/**
 * Created by domin on 30 Mar 2017.
 */

val pixel get() = Game.view.width / Game.window.width
val x48th get() = Game.view.width / 48.0
val y48th get() = Game.view.height / 48.0
val x12th get() = Game.view.width / 12.0
val y12th get() = Game.view.height / 12.0


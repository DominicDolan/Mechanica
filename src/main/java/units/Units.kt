@file:Suppress("unused") // There will be many functions here that go unused most of the time
package units

import display.Game

/**
 * Created by domin on 30 Mar 2017.
 */

val pixel get() = Game.viewWidth / Game.width
val x48th get() = Game.viewWidth / 48.0
val y48th get() = Game.viewHeight / 48.0
val x12th get() = Game.viewWidth / 12.0
val y12th get() = Game.viewHeight / 12.0


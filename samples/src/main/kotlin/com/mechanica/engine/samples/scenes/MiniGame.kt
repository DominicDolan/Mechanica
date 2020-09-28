package com.mechanica.engine.samples.scenes

import com.mechanica.engine.animation.AnimationFormulas
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.View
import com.mechanica.engine.game.view.intersects
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.scenes.addAnimation
import com.mechanica.engine.scenes.addExclusivelyActiveScenesNew
import com.mechanica.engine.scenes.scenes.UIScene
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.scenes.scenes.sprites.MovingSprite
import com.mechanica.engine.scenes.setNewMainScene
import com.mechanica.engine.unit.angle.Degree
import com.mechanica.engine.unit.angle.degrees
import com.mechanica.engine.unit.angle.plus
import com.mechanica.engine.unit.vector.*
import com.mechanica.engine.util.Timer
import com.mechanica.engine.util.extensions.fori

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { PlayScene() }
    }

    Game.loop()
}

class PlayScene : WorldScene() {
    private val player = addScene(Player())
    private val enemy = Array(10) { addScene(Enemy()) }

    private var gameOver = false

override fun update(delta: Double) {
    if (!gameOver) {
        if (player.hasHit(enemy)) {
            gameOver()
        }
    }
}

    private fun gameOver() {
        gameOver = true
        pauseSprites()
        addScene(GameOverScene())
    }

    private fun pauseSprites() {
        player.paused = true
        enemy.fori { it.paused = true }
    }
}

class Player : MovingSprite(), Inputs by Inputs.create() {
    private val speed = 2.0

    private val walkingState = WalkingState(this)
    private val invisibilityState = InvisibilityState(this)

    private val states = addExclusivelyActiveScenesNew(walkingState, invisibilityState)

    override fun update(delta: Double) {
        if (keyboard.space.hasBeenPressed) {
            invisibilityState.active = true
        }

        if (keyboard.up()) {
            position.y += delta * speed
        }
        if (keyboard.down()) {
            position.y -= delta * speed
        }
        if (keyboard.left()) {
            position.x -= delta * speed
        }
        if (keyboard.right()) {
            position.x += delta * speed
        }
    }

    fun hasHit(enemies: Array<Enemy>) = states.active.hasHit(enemies)

}

abstract class PlayerState(private val player: Player) : MovingSprite() {
    override val position: DynamicVector
        get() = player.position
    override val view: View
        get() = player.view

    abstract fun hasHit(enemies: Array<Enemy>): Boolean

    fun hasHit(enemy: Enemy) = view.intersects(enemy.view)
}

class WalkingState(player: Player) : PlayerState(player) {
    override fun hasHit(enemies: Array<Enemy>): Boolean {
        enemies.fori {
            if (hasHit(it)) {
                return true
            }
        }
        return false
    }

    override fun render(draw: Drawer) {
        draw.inView.blue.rectangle()
    }

}

class InvisibilityState(player: Player) : PlayerState(player) {
    private val invisibilityTimeLimit = 2.0
    private val rechargeTime = 5.0

    private var activeStart = Timer.now
    private val activeTimer : Double
        get() = Timer.now - activeStart

    private var activeEnd = activeStart - rechargeTime
    private val inactiveTimer : Double
        get() = Timer.now - activeEnd

    override fun hasHit(enemies: Array<Enemy>) = false

    override fun update(delta: Double) {
        if (activeTimer >= invisibilityTimeLimit) {
            active = false
            activeEnd = Timer.now
        }

        if (inactiveTimer < rechargeTime) {
            active = false
            return
        }
    }

    override fun render(draw: Drawer) {
        draw.inView.blue.alpha(0.3).rectangle()
    }

    override fun activated() {
        activeStart = Timer.now
    }
}

class Enemy : MovingSprite() {
    private val startingPosition: InlineVector
    private val direction: Degree

    private val speedVector: InlineVector
    private val travelDistance = 15.0

    init {
        val randomAngle = (Math.random() * 360.0).degrees

        // This will set the starting position to be a radius of 'travelDistance' away from the center at an angle of 'randomAngle'
        startingPosition = vec(travelDistance, randomAngle)
        position.set(startingPosition)

        // Set the travel direction to be approximately the opposite to the starting direction with just a bit of randomness
        direction = randomAngle + (160.0 + Math.random() * 40.0).degrees

        val speed = 1.0 + Math.random() * 2.0
        speedVector = vec(speed, direction)
    }

    override fun update(delta: Double) {

        // This is doing some vector maths to make the position change according to the speed
        val newPosition = vec(position) + speedVector * delta

        // If the position gets too far away, then move it back to the startingPosition
        if (newPosition.r > travelDistance) {
            position.set(startingPosition)
        } else {
            position.set(newPosition)
        }

    }

    override fun render(draw: Drawer) {
        draw.inView.red.rectangle()
    }
}

class GameOverScene : UIScene(2) {
    private val fadeIn = addAnimation(2.0, AnimationFormulas.linear(0.0, 1.0))

    private val keyboard = Keyboard.create()

    init {
        fadeIn.play()
    }

    override fun update(delta: Double) {
        if (keyboard.R.hasBeenPressed) {
            setNewMainScene { PlayScene() }
        }
    }

    override fun render(draw: Drawer) {
        draw.centered.ui.red.alpha(0.6 * fadeIn.value).rectangle(0.0, 0.0, camera.width, camera.height)
        draw.centered.black.alpha(fadeIn.value).text("GAME OVER", 1.5, 0.0, 0.0)
        draw.centered.black.alpha(fadeIn.value).text("Press R to restart", 0.5, 0.0, -3.0)
    }
}
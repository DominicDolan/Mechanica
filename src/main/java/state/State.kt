package state

import renderer.Painter

/**
 * Created by domin on 22 Mar 2017.
 */

abstract class State {
    abstract fun update(delta: Float)

    abstract fun render(g: Painter)


}

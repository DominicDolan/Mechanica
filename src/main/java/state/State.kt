package state

import renderer.Painter

/**
 * Created by domin on 22 Mar 2017.
 */

public abstract class State {
    //    public int width = G.WIDTH;
    //    public int height = G.HEIGHT;
    //    int centerX = width/2;
    //    int centerY = height/2;
    internal var PI = Math.PI.toFloat()
    public var initialised = false

    abstract fun init()

    abstract fun update(delta: Float)

    abstract fun render(g: Painter)


}

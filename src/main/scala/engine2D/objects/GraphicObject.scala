package engine2D.objects

import engine2D.GameEngine
import sfml.graphics.Transformable
import sfml.graphics.Drawable
import engine2D.graphics.GrUtils

/** A GraphicObject is a way to convert Drawable objects into GameObjects.
  * @param drawable
  *   The Drawable to draw when this GameObject is drawn.
  * @param engine
  *   The GameEngine that this GameObject belongs to.
  * @param active
  *   Whether or not this GameObject is active. If it's not active, it won't be
  *   updated or drawn.
  */
class GraphicObject(
    var drawable: Drawable,
    engine: GameEngine,
) extends GameObject(engine) {

  /** Will draw the drawable and call the super method. The super method will
    * draw the children of this GameObject.
    * @param target
    *   The RenderTarget to draw on.
    * @param states
    *   The RenderStates to use.
    */
  override def onDraw(
      target: sfml.graphics.RenderTarget,
      states: sfml.graphics.RenderStates
  ): Unit =
    target.draw(drawable, GrUtils.newState(states, transform))
    super.onDraw(target, GrUtils.newState(states, transform))
}

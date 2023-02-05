package engine2D.objects

import engine2D.GameEngine
import sfml.graphics.Transformable
import sfml.graphics.Drawable
import engine2D.graphics.GrUtils

/** A GraphicObject is a way to convert Drawable objects into GameObjects.
  */
class GraphicObject(
    var drawable: Drawable,
    engine: GameEngine,
    active: Boolean = true
) extends GameObject(engine, active) {
  override def onDraw(
      target: sfml.graphics.RenderTarget,
      states: sfml.graphics.RenderStates
  ): Unit =
    target.draw(drawable, GrUtils.newState(states, transform))
    super.onDraw(target, GrUtils.newState(states, transform))
}

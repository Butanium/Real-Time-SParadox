package engine.objects

import engine.GameInfo
import sfml.graphics.Drawable
import sfml.graphics.RenderTarget
import sfml.graphics.RenderStates
import sfml.graphics.Transformable

abstract class GameObject(var active: Boolean = true)
    extends Transformable
    with Drawable {
  def update(gameInfo: GameInfo): Unit
}

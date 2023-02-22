package engine2D.objects

import sfml.graphics.Transformable

/** A GameTransform is a Transformable with a few extra methods.
  */
class GameTransform extends Transformable {

  /** Returns distance to another GameTransform.
    * @param other
    *   The other GameTransform.
    */
  def distanceTo(other: Transformable) = {
    val dx = position.x - other.position.x
    val dy = position.y - other.position.y
    Math.sqrt(dx * dx + dy * dy)
  }

  def setOriginToCenter(localBounds: sfml.graphics.Rect[Float]) = {
    origin = (localBounds.width / 2, localBounds.height / 2)
  }

  def setOriginToCenter(width: Float, height: Float) = {
    origin = (width / 2, height / 2)
  }

  def setOriginToCenter(dimensions: sfml.system.Vector2[Float]) = {
    origin = (dimensions.x / 2, dimensions.y / 2)
  }

}

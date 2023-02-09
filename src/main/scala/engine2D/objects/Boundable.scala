package engine2D.objects

import sfml.graphics.Rect
import sfml.system.Vector2

/** An object that can be bounded. i.e
  */
trait Boundable {
  def contains(point: Vector2[Float]): Boolean
  def contains(x: Float, y: Float): Boolean = contains(Vector2(x, y))
}

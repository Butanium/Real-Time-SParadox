package engine2D.objects

import sfml.graphics.Rect
import sfml.system.Vector2

trait Boundable {
  def bounds: Rect[Float]
  def contains(point: Vector2[Float]): Boolean =
    bounds.contains(point.x, point.y)
  def contains(x: Float, y: Float): Boolean =
    bounds.contains(x, y)
}

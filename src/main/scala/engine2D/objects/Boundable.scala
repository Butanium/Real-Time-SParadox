package engine2D.objects

import sfml.graphics.Rect
import sfml.system.Vector2

/** An object that can be bounded. i.e you can check if a point is inside it.
  */
trait Boundable {

  /** Check if the object contains a point.
    * @param point
    *   The point to check.
    * @return
    *   True if the object contains the point, false otherwise.
    */
  def contains(point: Vector2[Float]): Boolean

  /** Check if the object contains a point.
    * @param x
    *   The x coordinate of the point to check.
    * @param y
    *   The y coordinate of the point to check.
    * @return
    *   True if the object contains the point, false otherwise.
    */
  def contains(x: Float, y: Float): Boolean = contains(Vector2(x, y))

  def order: (Int, Int)
}

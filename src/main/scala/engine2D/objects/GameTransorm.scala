package engine2D.objects

import sfml.graphics.Transformable

class GameTransorm extends Transformable {
  def distanceTo(other: Transformable) = {
    val dx = position.x - other.position.x
    val dy = position.y - other.position.y
    Math.sqrt(dx * dx + dy * dy)
  }
}

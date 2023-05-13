package engine2D.objects

import sfml.internal.system.Vector2
import sfml.system.Vector2

class LineObject(
    var thickness: Float,
    var target1: GameObject,
    var target2: GameObject,
    var addPos1: Vector2[Float],
    var addPos2: Vector2[Float],
    engine: engine2D.GameEngine
) extends ShapeObject(
      sfml.graphics.RectangleShape(1f, 1f),
      engine
    ) {
  val line = drawable.asInstanceOf[sfml.graphics.RectangleShape]
  override def onUpdate(): Unit = {
    var dx = (target1.position.x + addPos1.x) - (target2.position.x + addPos1.x)
    var dy = (target1.position.y + addPos1.y) - (target2.position.y + addPos2.y)
    var dist = Math.sqrt(dx * dx + dy * dy).toFloat
    val angle = Math.atan2(dy, dx)
    rotation = (angle * 180f / Math.PI + 180f).toFloat
    position = target1.position + addPos1
    scale = (dist, thickness)
    super.onUpdate()
  }

}

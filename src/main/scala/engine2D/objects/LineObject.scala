package engine2D.objects

import sfml.internal.system.Vector2

class LineObject(
    var thickness: Float,
    target1: GameObject,
    target2: GameObject,
    engine: engine2D.GameEngine
) extends ShapeObject(
      sfml.graphics.RectangleShape(1f, 1f),
      engine
    ) {
  val line = drawable.asInstanceOf[sfml.graphics.RectangleShape]
  line.origin = (0.5f, 0.5f)
  override def onUpdate(): Unit = {
    val dx = target2.position.x - target1.position.x
    val dy = target2.position.y - target1.position.y
    val angle = Math.atan2(dy, dx)
    position = target1.position
    rotation = (angle * 180f / Math.PI).toFloat
    scale = (Math.sqrt(dx * dx + dy * dy).toFloat, thickness)
    super.onUpdate()
  }
}

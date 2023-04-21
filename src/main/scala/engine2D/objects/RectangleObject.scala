package engine2D.objects

class RectangleObject(
    rectWidth: Float,
    rectHeight: Float,
    engine: engine2D.GameEngine
) extends ShapeObject(
      sfml.graphics.RectangleShape((rectWidth, rectHeight)),
      engine
    )
    with RectBounds {
  val rect = drawable.asInstanceOf[sfml.graphics.RectangleShape]
  def globalBounds = globalTransform.transformRect(
    sfml.graphics.Rect[Float](0, 0, rectWidth, rectHeight)
  )
  override def contains(point: sfml.system.Vector2[Float]): Boolean =
    globalBounds.contains(point)

}

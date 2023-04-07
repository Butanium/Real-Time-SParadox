package engine2D.objects

class RectangleObject(
    rectWidth: Float,
    rectHeight: Float,
    engine: engine2D.GameEngine
) extends GraphicObject(
      sfml.graphics.RectangleShape((rectWidth, rectHeight)),
      engine
    )
    with Boundable {
  val rect = drawable.asInstanceOf[sfml.graphics.RectangleShape]
  def fillColor = rect.fillColor
  def fillColor_=(c: sfml.graphics.Color) = rect.fillColor = c
  def outlineColor = rect.outlineColor
  def outlineColor_=(c: sfml.graphics.Color) = rect.outlineColor = c
  def outlineThickness = rect.outlineThickness
  def outlineThickness_=(t: Float) = rect.outlineThickness = t
  def bounds = globalTransform.transformRect(
    sfml.graphics.Rect[Float](0, 0, rectWidth, rectHeight)
  )
  override def contains(point: sfml.system.Vector2[Float]): Boolean =
    bounds.contains(point)
}

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
  /* def size = rect.size TODO: w8 for sfml update
   def size_=(s: sfml.system.Vector2[Float]) = rect.size = s
  def width = size.x
  def width_=(w: Float) = size = sfml.system.Vector2(w, height)
  def height = size.y
  def height_=(h: Float) = size = sfml.system.Vector2(width, h)*/
  // todo use bounds
  def bounds = globalTransform.transformRect(
    sfml.graphics.Rect[Float](0, 0, rectWidth, rectHeight)
  )
  override def contains(point: sfml.system.Vector2[Float]): Boolean =
    bounds.contains(point)
}

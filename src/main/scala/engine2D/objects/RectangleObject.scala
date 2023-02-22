package engine2D.objects

class RectangleObject(
    rectWidth: Float,
    rectHeight: Float,
    engine: engine2D.GameEngine
) extends GraphicObject(sfml.graphics.RectangleShape((1, 1)), engine)
    with Boundable {
  val rect = drawable
  scale = (rectWidth, rectHeight)
  def fillColor = rect.fillColor
  def fillColor_=(c: sfml.graphics.Color) = rect.fillColor = c
  def outlineColor = rect.outlineColor
  def outlineColor_=(c: sfml.graphics.Color) = rect.outlineColor = c
  def outlineThickness = rect.outlineThickness
  def outlineThickness_=(t: Float) = rect.outlineThickness = t
  def size = scale
  def size_=(s: sfml.system.Vector2[Float]) = scale = s
  def width = size.x
  def width_=(w: Float) = size = sfml.system.Vector2(w, height)
  def height = size.y
  def height_=(h: Float) = size = sfml.system.Vector2(width, h)
  def bounds = sfml.graphics.Rect[Float](position.x, position.y, width, height)
  override def contains(point: sfml.system.Vector2[Float]): Boolean =
    bounds.contains(point)
}

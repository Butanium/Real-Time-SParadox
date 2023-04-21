package engine2D.objects

class ShapeObject(shape: sfml.graphics.Shape, engine: engine2D.GameEngine)
    extends GraphicObject(shape, engine) {
  def fillColor = shape.fillColor
  def fillColor_=(c: sfml.graphics.Color) = shape.fillColor = c
  def outlineColor = shape.outlineColor
  def outlineColor_=(c: sfml.graphics.Color) = shape.outlineColor = c
  def outlineThickness = shape.outlineThickness
  def outlineThickness_=(t: Float) = shape.outlineThickness = t
}

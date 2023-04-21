package engine2D.objects

import sfml.graphics.Font
import engine2D.GameEngine
import sfml.graphics.Text
import engine2D.graphics.FontManager

class TextObject(
    textString: String,
    engine: GameEngine,
    fontFile: String = "basic.ttf",
    charSize: Int = 24
) extends GraphicObject(Text(), engine)
    with RectBounds {
  val text = drawable.asInstanceOf[Text]
  text.font = FontManager.getFont(fontFile)
  text.string = textString
  text.characterSize = charSize
  def fillColor = text.fillColor
  def fillColor_=(c: sfml.graphics.Color) = text.fillColor = c
  def outlineColor = text.outlineColor
  def outlineColor_=(c: sfml.graphics.Color) = text.outlineColor = c
  def string = text.string
  def string_=(s: String) = text.string = s
  def characterSize = text.characterSize
  def characterSize_=(s: Int) = text.characterSize = s
  def localBounds = text.localBounds
  def globalBounds = globalTransform.transformRect(localBounds)
  def contains(point: sfml.system.Vector2[Float]) = globalBounds.contains(point)

}

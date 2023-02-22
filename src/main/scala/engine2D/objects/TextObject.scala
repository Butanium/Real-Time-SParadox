package engine2D.objects

import sfml.graphics.Font
import engine2D.GameEngine
import sfml.graphics.Text

class TextObject(
    textString: String,
    engine: GameEngine,
    fontFile: String = "basic.ttf",
    charSize: Int = 24
) extends GameObject(engine) {
  val font = Font()
  font.loadFromFile("src/main/resources/fonts/" + fontFile)
  val text = Text()
  text.font = font
  text.string = textString
  text.characterSize = charSize
  def color = text.color
  def color_=(c: sfml.graphics.Color) = text.color = c
  def string = text.string
  def string_=(s: String) = text.string = s
  def characterSize = text.characterSize
  def characterSize_=(s: Int) = text.characterSize = s
  override def onDraw(
      target: sfml.graphics.RenderTarget,
      states: sfml.graphics.RenderStates
  ) = {
    target.draw(text, states)
    super.onDraw(target, states)
  }

}

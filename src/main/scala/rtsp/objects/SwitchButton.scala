package rtsp.objects

import engine2D.*
import engine2D.objects.*
import sfml.graphics.Color
import sfml.window.Mouse

class SwitchButton(shop0: Shop[?], shop1: Shop[?], engine: GameEngine)
    extends GameObject(engine) {
  val shops = Array(shop0, shop1)
  def switchShop() =
    shops.foreach(s => s.active = !s.active)
  shop0.active = true
  shop1.active = false
  position = (engine.window.size.x * 0.75f, 0f)
  val rectangle =
    RectangleObject(
      engine.window.size.x * 0.25f,
      engine.window.size.y * 0.08f,
      engine
    )
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 80)
  addChildren(rectangle)
  val text = new TextObject("Switch Shop", engine)
  text.fillColor = (Color(236, 191, 42))
  text.zIndex = 1
  addChildren(text)
  text.position = (engine.window.size.x * 0.02f, engine.window.size.y * 0.02f)
  listenToBoundsClicked(Mouse.Button.Left, rectangle, true, switchShop)

}

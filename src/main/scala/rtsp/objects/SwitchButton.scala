package rtsp.objects

import engine2D.*
import engine2D.objects.*
import sfml.graphics.Color
import sfml.window.Mouse

class SwitchButton(shop0 : Shop[?], shop1 : Shop[?], var currentShop : Int, engine : GameEngine) extends GameObject(engine){
  def switchShop() =
    if currentShop == 0 then 
      shop0.active = false
      shop1.active = true
      currentShop = 1
    else
      shop0.active = true
      shop1.active = false
      currentShop = 0

  position = (engine.window.size.x * 0.85f, 0f)
  val rectangle =
    RectangleObject(engine.window.size.x * 0.15f, engine.window.size.y * 0.15f, engine)
  rectangle.outlineColor = Color(236, 151, 22)
  rectangle.outlineThickness = 5
  rectangle.fillColor = Color(165, 245, 73, 20)
  addChildren(rectangle)
  val text = new TextObject("Switch Shop", engine)
  text.fillColor = (Color(236, 191, 42))
  addChildren(text)
  text.position =
    (engine.window.size.x * 0.02f, engine.window.size.y * 0.1f)
  listenToBoundsClicked(Mouse.Button.Left, rectangle, true, switchShop)
  
}

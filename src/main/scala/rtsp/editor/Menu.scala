package rtsp.editor

import engine2D.objects.GameObject
import engine2D.GameEngine
import engine2D.objects.RectangleObject
import rtsp.Constants
import engine2D.Game
import engine2D.objects.ButtonObject

class Menu(var uiParent: Option[Menu], engine: GameEngine)
    extends GameObject(engine) {
  val background = RectangleObject(
    engine.window.size.x.toFloat,
    engine.window.size.y.toFloat,
    engine
  )
  background.fillColor = sfml.graphics.Color(0, 0, 0, 150)
  addChildren(background)
  val goToParentButton = new ButtonObject(
    if (uiParent.isDefined) "Back" else "Close",
    () => {
      active = false
      onClose()
      uiParent.foreach(_.active = true)
    },
    engine
  )
  goToParentButton.zIndex = 1
  addChildren(goToParentButton)
  var onClose: () => Unit = () => ()
  

  def open(from: Option[Menu] = None): Unit = {
    active = true
    uiParent = from.orElse(uiParent)
    uiParent.foreach(_.active = false)
  }

}

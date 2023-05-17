package rtsp.editor

import engine2D.objects.GameObject
import engine2D.GameEngine
import engine2D.objects.RectangleObject
import rtsp.Constants
import engine2D.Game
import engine2D.objects.ButtonObject
import sfml.window.Mouse.Button
import engine2D.eventHandling.MouseEvent

class Menu(private var _uiParent: Option[Menu], engine: GameEngine)
    extends GameObject(engine) {
  def uiParent = _uiParent
  def uiParent_=(value: Option[Menu]): Unit = {
    _uiParent = value
    if (value.isDefined) active = false
    goToParentButton.textObject.text.string =
      if (value.isDefined) "Back" else "Close"

  }
  val background = RectangleObject(
    engine.window.size.x.toFloat,
    engine.window.size.y.toFloat,
    engine
  )
  // Make sure that no mouse event is propagated what is behind the menu
  background.listenToMouseEvent(
    MouseEvent.MouseInBounds(background, true),
    () => ()
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
  var onOpen: () => Unit = () => ()
  def open(): Unit = {
    onOpen()
    active = true
    uiParent.foreach(_.active = false)
  }

}

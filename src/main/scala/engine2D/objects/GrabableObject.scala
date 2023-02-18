package engine2D.objects

import engine2D.eventHandling.MouseEvent.*
import sfml.system.Vector2

abstract class GrabableObject (
    // val toMove: GameObject,
    val button: sfml.window.Mouse.Button,
    engine: engine2D.GameEngine
) extends GameObject(engine) with Boundable {
  def contains(point: Vector2[Float]): Boolean 
  var onRelease: () => Unit = () => ()
  def setOnRelease(action: () => Unit): Unit =
    onRelease = action
  var onGrab: () => Unit = () => ()
  def setOnGrab(action: () => Unit): Unit =
    onGrab = action

  private val pressEvent = BoundsPressed(this, button, true)
  private val releaseEvent = ButtonReleased(button, true)
  private val moveEvent = MouseMoved(true)
  releaseEvent.active = false
  moveEvent.active = false
  private def pressedAction(): Unit =
    pressEvent.active = false
    releaseEvent.active = true
    moveEvent.active = true
    this.onGrab()

  private def releasedAction(): Unit =
    pressEvent.active = true
    releaseEvent.active = false
    moveEvent.active = false
    this.onRelease()

  private def movedAction(): Unit =
    this.position = engine.mouseState.worldPos

  engine.mouseManager.registerMouseEvent(pressEvent, pressedAction)
  engine.mouseManager.registerMouseEvent(releaseEvent, releasedAction)
  engine.mouseManager.registerMouseEvent(moveEvent, movedAction)

  override def active_=(value: Boolean) =
    if value != active then
      if value then
        pressEvent.active = true
        releaseEvent.active = false
        moveEvent.active = false
      else
        pressEvent.active = false
        if releaseEvent.active then releasedAction()
        releaseEvent.active = false
        moveEvent.active = false
    super.active = value

  override def onDeletion(): Unit =
    pressEvent.toRemove = true
    releaseEvent.toRemove = true
    moveEvent.toRemove = true
    super.onDeletion()

}

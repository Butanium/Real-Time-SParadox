package engine2D.objects

import engine2D.eventHandling.MouseEvent.*
import sfml.system.Vector2
import engine2D.eventHandling.MouseEvent
import scala.collection.mutable.ListBuffer

/** A Grabable is a GameObject that can be grabbed by the mouse.
  * @param button
  *   The button that will be used to grab the object.
  * @param engine
  *   The GameEngine that will be used to register the mouse events.
  * @param debug
  *   If true, the object will print debug messages.
  * @note
  *   To implement a Grabable, you need to implement the contains method as it
  *   extends Boundable. The listeners and position fields are already
  *   implemented if you use this trait on a GameObject. If you don't, you need
  *   to implement them yourself (but you should use a GameObject anyway)
  */
trait Grabbable(
    // val toMove: GameObject,
    val button: sfml.window.Mouse.Button,
    engine: engine2D.GameEngine,
    debug: Boolean = false
) extends Boundable {
  private var grabbable: Boolean = true
  val listeners: ListBuffer[MouseEvent]
  var position: Vector2[Float]
  def contains(point: Vector2[Float]): Boolean
  private var onRelease: () => Unit = () =>
    if debug then println("game object released")
  def setOnRelease(action: () => Unit): Unit =
    onRelease = () => {
      if debug then println("game object released")
      action()
    }
  private var onGrab: () => Unit = () =>
    if debug then println("game object grabbed")
  def setOnGrab(action: () => Unit): Unit =
    onGrab = () => {
      if debug then println("game object grabbed")
      action()
    }

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
    if debug then
      println(
        f"gameObject moved from $position to ${engine.mouseState.worldPos}"
      )
    this.position = engine.mouseState.worldPos

  engine.mouseManager.registerMouseEvent(pressEvent, pressedAction)
  engine.mouseManager.registerMouseEvent(releaseEvent, releasedAction)
  engine.mouseManager.registerMouseEvent(moveEvent, movedAction)
  listeners += (pressEvent, releaseEvent, moveEvent)
  def isGrabbable: Boolean = grabbable
  def isGrabbable_=(value: Boolean) =
    if value != grabbable then
      if value then {
        pressEvent.active = true
        releaseEvent.active = false
        moveEvent.active = false
      } else {
        if releaseEvent.active then onRelease()
        pressEvent.active = false
        releaseEvent.active = false
        moveEvent.active = false
      }

}

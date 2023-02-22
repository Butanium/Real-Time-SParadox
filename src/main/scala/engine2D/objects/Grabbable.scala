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

  /** Sets the action to perform when the object is released.
    * @param action
    *   The action to perform.
    */
  def setOnRelease(action: () => Unit): Unit =
    onRelease = () => {
      if debug then println("game object released")
      action()
    }
  private var onGrab: () => Unit = () =>
    if debug then println("game object grabbed")

  /** Sets the action to perform when the object is grabbed.
    * @param action
    *   The action to perform.
    */
  def setOnGrab(action: () => Unit): Unit =
    onGrab = () => {
      if debug then println("game object grabbed")
      action()
    }
  private var onMoveTo: Vector2[Float] => Vector2[Float] = pos => pos

  /** Sets the action to perform when the object is moved to a new position.
    * @param action
    *   The action to perform. It takes the new position as a parameter and
    *   returns the new position (which can be different from the parameter).
    * @note
    *   Changing the position can be useful if you want to snap the object to
    *   the grid or restrict its movement.
    */
  def setOnMoveTo(action: Vector2[Float] => Vector2[Float]): Unit =
    onMoveTo = pos => {
      val newPos = action(pos)
      if debug then println(f"game object moved to $newPos instead of $pos")
      newPos
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

  /** Whether or not the object is grabbable.
    */
  def isGrabbable: Boolean = grabbable

  /** Sets whether or not the object is grabbable.
    * @param value
    *   The new value of grabbable.
    * @note
    *   If you set grabbable to false while the object is grabbed, it will
    *   trigger the onRelease action.
    */
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

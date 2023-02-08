package engine2D.eventHandling

import sfml.graphics.Rect
import sfml.window.Mouse.Button
import scala.collection.mutable.Queue

/** Enum for mouse events that can be handled
  * @param isPermanent
  *   whether the event is permanent or not. If it's not permanent, it will be
  *   removed from the event queue once it's triggered
  * @param eventId
  *   the unique id of the event. Used for comparison
  * @param active
  *   whether the event is active or not. If it's not active, it won't be
  *   triggered even if the conditions are met
  * @note
  *   I'm not sure if we'll really need eventId and active
  */
enum MouseEvent(
    val isPermanent: Boolean,
    val eventId: Int,
    var active: Boolean = true
) {

  /** Triggered when the mouse is moved
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case MouseMoved(_isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is pressed
    * @param button
    *   the button that has to be pressed to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case ButtonPressed(val button: Button, _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is released
    * @param button
    *   the button that has to be released to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case ButtonReleased(val button: Button, _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())
  // Clicked would be a combination of pressed and released
  // I don't think it's necessary but I'll leave it here for now
  // case ButtonClicked(val button: Button)
  // case BoundClicked(val bound: Rect[Float], val button: Button)

  /** Triggered when the mouse enters a bound
    * @param bound
    *   the bound that the mouse has to enter to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case MouseInBound(val bound: Rect[Float], _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when the mouse leaves a bound
    * @param bound
    *   the bound that the mouse has to leave to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case MouseOutBound(val bound: Rect[Float], _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is pressed while the mouse is in a bound
    * @param bound
    *   the bound that the mouse has to be in to trigger the event
    * @param button
    *   the button that has to be pressed to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case BoundPressed(
      val bound: Rect[Float],
      val button: Button,
      _isPermanent: Boolean
  ) extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is released while the mouse is in a bound
    * @param bound
    *   the bound that the mouse has to be in to trigger the event
    * @param button
    *   the button that has to be released to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case BoundReleased(
      val bound: Rect[Float],
      val button: Button,
      _isPermanent: Boolean
  ) extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  override def equals(x: Any): Boolean =
    x match
      case x: MouseEvent => x.eventId == this.eventId
      case _             => false
}

object MouseEvent:
  private var lastId = 0
  def getNewEventId(): Int =
    lastId += 1
    lastId

package engine2D.eventHandling

import engine2D.objects.Boundable
import sfml.window.Mouse.Button

/** Enum for mouse events that can be handled
  * @param isPermanent
  *   whether the event is permanent or not. If it's not permanent, it will be
  *   removed from the event queue once it's triggered
  * @param eventId
  *   the unique id of the event. Used for comparison
  * @param active
  *   whether the event is active or not. If it's not active, it won't be
  *   triggered even if the conditions are met
  * @param toRemove
  *   whether the event has to be removed from the event queue
  */
enum MouseEvent(
    val isPermanent: Boolean,
    val eventId: Int,
    var active: Boolean = true,
    var toRemove: Boolean = false
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
  // case BoundClicked(val bounds: Boundable, val button: Button)

  /** Triggered when the mouse enters a bounds
    * @param bounds
    *   the bounds that the mouse has to enter to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case MouseInBounds(val bounds: Boundable, _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when the mouse leaves a bounds
    * @param bounds
    *   the bounds that the mouse has to leave to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case MouseOutBounds(val bounds: Boundable, _isPermanent: Boolean)
      extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is pressed while the mouse is in a bounds
    * @param bounds
    *   the bounds that the mouse has to be in to trigger the event
    * @param button
    *   the button that has to be pressed to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case BoundsPressed(
      val bounds: Boundable,
      val button: Button,
      _isPermanent: Boolean
  ) extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** Triggered when a button is released while the mouse is in a bounds
    * @param bounds
    *   the bounds that the mouse has to be in to trigger the event
    * @param button
    *   the button that has to be released to trigger the event
    * @param _isPermanent
    *   whether the event is permanent or not. If it's not permanent, it will be
    *   removed from the event queue once it's triggered
    */
  case BoundsReleased(
      val bounds: Boundable,
      val button: Button,
      _isPermanent: Boolean
  ) extends MouseEvent(_isPermanent, MouseEvent.getNewEventId())

  /** The equals method for mouse events
    * @param x
    *   the object to compare to
    * @return
    *   whether the two objects are equal or not
    * @note
    *   Two mouse events are equal if they have the same id
    */
  override def equals(x: Any): Boolean =
    x match
      case x: MouseEvent => x.eventId == this.eventId
      case _             => false
}

object MouseEvent:
  private var lastId = 0
  def isZindexSensitive(event: MouseEvent): Boolean =
    event match
      case MouseEvent.BoundsPressed(_, _, _)  => true
      case MouseEvent.BoundsReleased(_, _, _) => false
      case MouseEvent.MouseInBounds(_, _)     => true
      case MouseEvent.MouseOutBounds(_, _)    => false
      case _                                  => false

  def order(event : MouseEvent): (Int, Int) =
    event match
      case MouseEvent.BoundsPressed(bound, _, _)  => bound.order
      case MouseEvent.MouseInBounds(bound, _)     => bound.order
      case _                                  => (0, 0)
  /** Returns a new unique id for a mouse event
    */
  private def getNewEventId(): Int =
    lastId += 1
    lastId

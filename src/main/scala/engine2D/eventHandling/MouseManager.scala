package engine2D.eventHandling
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.LinkedHashMap
import sfml.system.Vector2
import sfml.window.Event
import sfml.graphics.RenderWindow
import sfml.window.Mouse.Button
import engine2D.eventHandling.MouseEvent.*
import sfml.window.Mouse
import sfml.graphics.Rect

/** Enum to indicate the condition to check for an event. This is used to group
  * events that are checked in the same way.
  */
private enum EventCheckCondition {

  /** The mouse position has changed
    */
  case NewPosition

  /** A button has been pressed
    * @param button
    *   the button that has been pressed
    */
  case CheckButtonPressed(button: Button)

  /** A button has been released
    * @param button
    *   the button that has been released
    */
  case CheckButtonReleased(button: Button)
}

/** Companion object for EventCheckCondition
  */
private object EventCheckCondition {

  /** Returns the condition to check for an event
    *
    * @param event
    * @return
    *   the condition to check for the event
    */
  def from(event: MouseEvent): EventCheckCondition = event match {
    case MouseMoved(_) => EventCheckCondition.NewPosition
    case ButtonPressed(button, _) =>
      EventCheckCondition.CheckButtonPressed(button)
    case ButtonReleased(button, _) =>
      EventCheckCondition.CheckButtonReleased(button)
    case MouseInBound(_, _)  => EventCheckCondition.NewPosition
    case MouseOutBound(_, _) => EventCheckCondition.NewPosition
    case BoundPressed(_, button, _) =>
      EventCheckCondition.CheckButtonPressed(button)
    case BoundReleased(_, button, _) =>
      EventCheckCondition.CheckButtonReleased(button)
  }
}

/** Handles mouse events
  *
  * @param window
  *   the window to handle events for
  * @param debug
  *   whether to print debug messages
  */
class MouseManager(val window: RenderWindow, val debug: Boolean = false) {

  /** Stores the state of the mouse
    */
  val mouseState = new MouseState(window)

  /** Stores the events that are checked every frame
    */
  private val mouseEvents: LinkedHashMap[EventCheckCondition, ListBuffer[
    (MouseEvent, () => Unit)
  ]] =
    LinkedHashMap
      .empty[EventCheckCondition, ListBuffer[(MouseEvent, () => Unit)]]

  /** Stores the events that are added during the frame
    */
  private val newMouseEvents =
    LinkedHashMap
      .empty[EventCheckCondition, ListBuffer[(MouseEvent, () => Unit)]]

  /** Registers a new mouse event. The event will be checked every frame,
    * starting from the next frame
    *
    * @param newEvent
    *   the event to register
    * @param function
    *   the function to call when the event is triggered
    */
  def registerMouseEvent(newEvent: MouseEvent, function: () => Unit): Unit =
    newMouseEvents.getOrElseUpdate(
      EventCheckCondition.from(newEvent),
      ListBuffer.empty
    ) += ((newEvent, function))

  /** Adds the new events to the list of events to check
    */
  def mergeNewEvents(): Unit = {
    for ((condition, events) <- newMouseEvents) {
      mouseEvents.getOrElseUpdate(condition, ListBuffer.empty) ++= events
    }
    newMouseEvents.clear()
  }

  /** Handles a mouse event
    * @param event
    *   the event to handle
    */
  def handleEvent(event: sfml.window.Event): Unit =
    event match {
      case Event.MouseMoved(x, y) => handleMouseMoved(x, y)
      case Event.MouseButtonPressed(button, x, y) =>
        handleMousePressed(button, x, y)
      case Event.MouseButtonReleased(button, x, y) =>
        handleMouseReleased(button, x, y)
      case _ => ()
    }

  private def filterAndTrigger(
      condition: EventCheckCondition,
      predicate: MouseEvent => Boolean
  ): Unit =
    mouseEvents
      .getOrElse(condition, ListBuffer.empty)
      .filterInPlace((event, function) =>
        if (predicate(event)) {
          function()
          event.isPermanent
        } else true
      )

  private def isTriggeredByMove(
      event: MouseEvent
  ): Boolean =
    event match {
      case MouseMoved(_) => true
      case MouseInBound(bound, _) =>
        bound.contains(mouseState.getWorldPos())
      case MouseOutBound(bound, _) =>
        !bound.contains(mouseState.getWorldPos())
      case _ =>
        throw new Exception("Impossible mouse event")
    }

  private def handleMouseMoved(x: Int, y: Int): Unit = {
    mouseState.setMousePos(x, y)
    filterAndTrigger(EventCheckCondition.NewPosition, isTriggeredByMove)
  }

  private def isTriggeredByButton(
      event: MouseEvent,
      button: Button,
      eventPos: Vector2[Float]
  ): Boolean =
    event match {
      case ButtonPressed(b, _)  => b == button
      case ButtonReleased(b, _) => b == button
      case BoundPressed(bound, b, _) =>
        b == button && bound.contains(eventPos.x, eventPos.y)
      case _ =>
        throw new Exception("Impossible button event")
    }

  private def handleMousePressed(button: Button, x: Int, y: Int): Unit = {
    mouseState.pressButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    filterAndTrigger(
      EventCheckCondition.CheckButtonPressed(button),
      isTriggeredByButton(_, button, eventPos)
    )
  }

  private def handleMouseReleased(button: Button, x: Int, y: Int): Unit = {
    mouseState.releaseButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    filterAndTrigger(
      EventCheckCondition.CheckButtonReleased(button),
      isTriggeredByButton(_, button, eventPos)
    )
  }

  /** Registers an even that will be triggered when a bound is clicked and the
    * mouse is released inside the bound. If the mouse is released outside the
    * bound, the function will not be called
    *
    * @param button
    *   the button to check
    * @param bound
    *   the bound to check
    * @param isPermanent
    *   whether the click event should be permanent
    * @param function
    *   the function to call when the event is triggered
    * @return
    *   the event that was registered
    * @note
    *   In most cases, you'll certainly ignore the return value
    */
  def registerBoundClickedEvent(
      button: Button,
      bound: Rect[Float],
      isPermanent: Boolean,
      function: () => Unit
  ): MouseEvent =
    val pressEvent = BoundPressed(bound, button, false)
    val releaseEvent = ButtonReleased(button, false)
    def onReleaseEvent(): Unit =
      if (bound.contains(mouseState.getWorldPos())) function()
      if isPermanent then registerMouseEvent(pressEvent, onPressEvent)
    def onPressEvent(): Unit =
      registerMouseEvent(releaseEvent, onReleaseEvent)
    registerMouseEvent(pressEvent, onPressEvent)
    pressEvent

}

package engine2D.eventHandling
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.LinkedHashMap
import sfml.system.Vector2
import sfml.window.Event
import sfml.graphics.RenderWindow
import sfml.window.Mouse.Button
import engine2D.eventHandling.MouseEvent.*
import sfml.window.Mouse
import engine2D.objects.Boundable

/** Enum to indicate the condition to check for an event. This is used to group
  * events that are checked in the same way.
  */
private enum EventCheckCondition {

  /** The mouse position has changed
    */
  case CheckNewPosition

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
    case MouseMoved(_) => EventCheckCondition.CheckNewPosition
    case ButtonPressed(button, _) =>
      EventCheckCondition.CheckButtonPressed(button)
    case ButtonReleased(button, _) =>
      EventCheckCondition.CheckButtonReleased(button)
    case MouseInBounds(_, _)  => EventCheckCondition.CheckNewPosition
    case MouseOutBounds(_, _) => EventCheckCondition.CheckNewPosition
    case BoundsPressed(_, button, _) =>
      EventCheckCondition.CheckButtonPressed(button)
    case BoundsReleased(_, button, _) =>
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
  private def mergeNewEvents(): Unit = {
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
    mergeNewEvents()
    event match {
      case Event.MouseMoved(x, y) =>
        if (debug) println(s"Mouse moved to $x, $y")
        handleMouseMoved(x, y)
      case Event.MouseButtonPressed(button, x, y) =>
        if (debug) println(s"Button $button pressed")
        handleMousePressed(button, x, y)
      case Event.MouseButtonReleased(button, x, y) =>
        if (debug) println(s"Button $button released")
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
        if (predicate(event) && event.active && !event.toRemove) {
          function()
          event.isPermanent
        } else !event.toRemove
      )

  private def isTriggeredByMove(
      event: MouseEvent
  ): Boolean =
    event match {
      case MouseMoved(_) => true
      case MouseInBounds(boundable, _) =>
        boundable.contains(mouseState.worldPos)
      case MouseOutBounds(boundable, _) =>
        !boundable.contains(mouseState.worldPos)
      case _ =>
        throw new Exception("Impossible mouse event")
    }

  private def handleMouseMoved(x: Int, y: Int): Unit = {
    mouseState.mousePos = Vector2(x, y)
    if debug then
      println(
        s"${mouseEvents.getOrElse(EventCheckCondition.CheckNewPosition, List.empty).size} mouse moved events to check"
      )
    filterAndTrigger(EventCheckCondition.CheckNewPosition, isTriggeredByMove)
  }

  private def isTriggeredByButton(
      event: MouseEvent,
      button: Button,
      eventPos: Vector2[Float]
  ): Boolean =
    event match {
      case ButtonPressed(b, _)  => b == button
      case ButtonReleased(b, _) => b == button
      case BoundsPressed(boundable, b, _) =>
        b == button && boundable.contains(eventPos.x, eventPos.y)
      case _ =>
        throw new Exception("Impossible button event")
    }

  private def handleMousePressed(button: Button, x: Int, y: Int): Unit = {
    mouseState.pressButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    if debug then
      println(
        s"${mouseEvents.getOrElse(EventCheckCondition.CheckButtonPressed(button), List.empty).size} mouse pressed events to check"
      )
    filterAndTrigger(
      EventCheckCondition.CheckButtonPressed(button),
      isTriggeredByButton(_, button, eventPos)
    )
  }

  private def handleMouseReleased(button: Button, x: Int, y: Int): Unit = {
    mouseState.releaseButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    if debug then
      println(
        s"${mouseEvents.getOrElse(EventCheckCondition.CheckButtonReleased(button), List.empty).size} mouse released events to check"
      )
    filterAndTrigger(
      EventCheckCondition.CheckButtonReleased(button),
      isTriggeredByButton(_, button, eventPos)
    )
  }

  /** Registers an event that will be triggered when a bounds is clicked and the
    * mouse is released inside the bounds. If the mouse is released outside the
    * bounds, the function will not be called
    *
    * @param button
    *   the button to check
    * @param bounds
    *   the bounds to check
    * @param isPermanent
    *   whether the click event should be permanent
    * @param function
    *   the function to call when the event is triggered
    * @return
    *   the event that was registered
    */
  def registerBoundClickedEvent(
      button: Button,
      boundable: Boundable,
      isPermanent: Boolean,
      function: () => Unit
  ): (MouseEvent, MouseEvent) =
    val pressEvent = BoundsPressed(boundable, button, isPermanent)
    val releaseEvent = ButtonReleased(button, isPermanent)
    releaseEvent.active = false
    def onReleaseEvent(): Unit =
      releaseEvent.active = false
      if isPermanent then pressEvent.active = true
      if (boundable.contains(mouseState.worldPos)) function()
    def onPressEvent(): Unit =
      pressEvent.active = false
      releaseEvent.active = true
    registerMouseEvent(pressEvent, onPressEvent)
    registerMouseEvent(releaseEvent, onReleaseEvent)
    (pressEvent, releaseEvent)

  /** Registers an event that will alternate between onFlip and onFlop when the
    * button is clicked
    *
    * @param button
    *   the button to check
    * @param bounds
    *   the bounds to check
    * @param onFlip
    *   the function to call when the event is triggered
    * @param onFlop
    *   the function to call when the event is triggered
    * @return
    *   the events that were registered
    */
  def registerClickBoundFlipFlopEvent(
      button: Button,
      boundable: Boundable,
      onFlip: () => Unit,
      onFlop: () => Unit
  ): (MouseEvent, MouseEvent, MouseEvent, MouseEvent) = {
    lazy val (flipPressed, flipReleased) =
      registerBoundClickedEvent(button, boundable, true, flip)
    lazy val (flopPressed, flopReleased) =
      registerBoundClickedEvent(button, boundable, true, flop)
    def flip(): Unit = {
      onFlip()
      flipPressed.active = false
      flopPressed.active = true
    }
    def flop(): Unit = {
      onFlop()
      flopPressed.active = false
      flipPressed.active = true
    }
    flopPressed.active = false
    (flipPressed, flipReleased, flopPressed, flopReleased)

  }
}

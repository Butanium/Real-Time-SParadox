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
  private val mouseEvents: ListBuffer[
    (MouseEvent, () => Unit)
  ] =
    ListBuffer[(MouseEvent, () => Unit)]()

  /** Stores the events that are added during the frame
    */
  private val newMouseEvents = ListBuffer[(MouseEvent, () => Unit)]()

  /** Registers a new mouse event. The event will be checked every frame,
    * starting from the next frame
    *
    * @param newEvent
    *   the event to register
    * @param function
    *   the function to call when the event is triggered
    */
  def registerMouseEvent(newEvent: MouseEvent, function: () => Unit): Unit =
    newMouseEvents += ((newEvent, function))

  /** Adds the new events to the list of events to check
    */
  private def mergeNewEvents(): Unit = {
    mouseEvents ++= newMouseEvents
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
        handleMouseMoved(x, y)
      case Event.MouseButtonPressed(button, x, y) =>
        handleMousePressed(button, x, y)
      case Event.MouseButtonReleased(button, x, y) =>
        handleMouseReleased(button, x, y)
      case _ => ()
    }

  private def filterAndTrigger(
      isTriggered: MouseEvent => Boolean
  ): Unit =
    var (sensitive, others) =
      mouseEvents.partition((event, _) => MouseEvent.isZindexSensitive(event))
    val othersToRemove = others.filter((event, function) =>
      if (event.active && !event.toRemove && isTriggered(event)) then
        function()
        !event.isPermanent
      else event.toRemove
    )
    val triggered = sensitive
      .filter((event, _) =>
        event.active && !event.toRemove && isTriggered(event)
      )
    val sensitiveToRemove =
      triggered.maxByOption((event, _) => MouseEvent.order(event)) match
        case None => ListBuffer.empty
        case Some((event, function)) =>
          val max = MouseEvent.order(event)
          triggered
            .filter((event, function) => {
              if MouseEvent.order(event) == max then
                function()
                !event.isPermanent
              else false
            })

    mouseEvents --= othersToRemove
    mouseEvents --= sensitiveToRemove
    mouseEvents --= sensitive.filter((event, _) => event.toRemove)

  private def isTriggeredByMove(
      event: MouseEvent
  ): Boolean =
    event match {
      case MouseMoved(_) => true
      case MouseInBounds(boundable, _) =>
        boundable.contains(mouseState.worldPos)
      case MouseOutBounds(boundable, _) =>
        !boundable.contains(mouseState.worldPos)
      case _ => false
    }

  private def handleMouseMoved(x: Int, y: Int): Unit = {
    mouseState.mousePos = Vector2(x, y)
    filterAndTrigger(isTriggeredByMove)
  }

  private def isTriggeredByButton(
      event: MouseEvent,
      button: Button,
      isPressed: Boolean,
      eventPos: Vector2[Float]
  ): Boolean =
    event match {
      case ButtonPressed(b, _)  => b == button && isPressed
      case ButtonReleased(b, _) => b == button && !isPressed
      case BoundsPressed(boundable, b, _) =>
        b == button && boundable.contains(eventPos.x, eventPos.y) && isPressed
      case BoundsReleased(boundable, b, _) =>
        b == button && boundable.contains(eventPos.x, eventPos.y) && !isPressed
      case _ => false
    }

  private def handleMousePressed(button: Button, x: Int, y: Int): Unit = {
    mouseState.pressButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    filterAndTrigger(
      isTriggeredByButton(_, button, true, eventPos)
    )
  }

  private def handleMouseReleased(button: Button, x: Int, y: Int): Unit = {
    mouseState.releaseButton(button)
    val eventPos = window.mapPixelToCoords(Vector2(x, y))
    filterAndTrigger(
      isTriggeredByButton(_, button, false, eventPos)
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

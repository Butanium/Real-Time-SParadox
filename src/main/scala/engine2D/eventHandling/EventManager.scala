package engine2D.eventHandling

import sfml.graphics.RenderWindow
import sfml.window.Event

/** Manages all events
  *
  * @param window
  *   the window to get events from
  * @param mouseManager
  *   the mouse manager to handle mouse events
  * @param debug
  *   if true, prints debug information
  */
class EventManager(
    val window: RenderWindow,
    val mouseManager: MouseManager,
    val debug: Boolean = false
) {

  /** Polls events and handles them
    */
  def handleEvents(): Unit =
    for event <- window.pollEvent() do
      event match
        case _: Event.Closed => window.closeWindow()
        case _               => mouseManager.handleEvent(event)

}

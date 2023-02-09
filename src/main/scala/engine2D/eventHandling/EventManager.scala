package engine2D.eventHandling

import sfml.graphics.RenderWindow
import sfml.window.Event

class EventManager(
    val window: RenderWindow,
    val mouseManager: MouseManager,
    val debug: Boolean = false
) {
  def handleEvents(): Unit =
    for event <- window.pollEvent() do
      event match
        case _: Event.Closed => window.closeWindow()
        case _               => mouseManager.handleEvent(event)

}

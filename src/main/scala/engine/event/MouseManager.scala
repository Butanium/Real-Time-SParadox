package engine.event
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.LinkedHashMap
import sfml.system.Vector2
import sfml.window.Event

class MouseManager(window: sfml.window.Window) {
  // Can be OPTImized
  val mouseEvents: LinkedHashMap[MouseEvent, () => Unit] =
    LinkedHashMap.empty[MouseEvent, () => Unit]

  def addMouseEvent(event: MouseEvent, function: () => Unit): Unit =
    mouseEvents += (event -> function)

  def handleEvent(event: sfml.window.Event, mousePos: Vector2[Float]): Unit =
    event match {
      case Event.MouseMoved(x, y) => handleMouseMoved(x, y)
      case Event.MouseButtonPressed(button, x, y) =>
        handleMousePressed(button, x, y)
      case Event.MouseButtonReleased(button, x, y) =>
        handleMouseReleased(button, x, y)
      case _ => ()
    }

  def handleMouseMoved(x: Int, y: Int): Unit = {
    val mousePos = window.mapPixelToCoords(x, y)

  }

}

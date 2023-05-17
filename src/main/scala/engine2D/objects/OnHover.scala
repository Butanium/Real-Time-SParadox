package engine2D.objects

import engine2D.GameEngine
import engine2D.eventHandling.MouseEvent

trait OnHover {
  def listenToMouseEvent(mouseEvent: MouseEvent, action: () => Unit): Unit
  def initShowOnHover(toShow: GameObject, bounds: Boundable) =
    toShow.active = false
    listenToMouseEvent(
      MouseEvent.MouseInBounds(bounds, true),
      () => {
        toShow.active = true
      }
    )
    listenToMouseEvent(
      MouseEvent.MouseOutBounds(bounds, true),
      () => {
        toShow.active = false
      }
    )
}

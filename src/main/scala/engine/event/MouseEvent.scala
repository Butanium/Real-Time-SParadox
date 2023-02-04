package engine.event

import sfml.graphics.Rect
import sfml.window.Mouse.Button

enum MouseEvent {
  case BoundClicked(val bound: Rect[Float], val button: Button)
  case BoundEntered(val bound: Rect[Float])
  case BoundExited(val bound: Rect[Float])
  case ButtonReleased(val button: Button)
  case ButtonClicked(val button: Button)
  case ButtonPressed(val button: Button)
  case MouseMoved

}

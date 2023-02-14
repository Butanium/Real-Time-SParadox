package rtsp

import engine2D.Game
import sfml.graphics.RenderWindow

class RTSPGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(window, debug = false)
}

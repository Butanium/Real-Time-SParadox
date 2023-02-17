package rtsp

import engine2D.Game
import sfml.graphics.RenderWindow

class RTSPGame(window: RenderWindow)
    extends Game(window, 60, sfml.graphics.Color.Black(), debug = false) {
  val engine = new RTSPGameEngine(1f/60, window, debug = false)
}

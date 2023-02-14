package rtsp

import engine2D.GameEngine
import sfml.graphics.RenderWindow

class RTSPGameEngine(window: RenderWindow, debug: Boolean = false)
    extends GameEngine(1f, window, debug = debug) {}

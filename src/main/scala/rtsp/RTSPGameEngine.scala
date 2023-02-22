package rtsp

import engine2D.GameEngine
import sfml.graphics.RenderWindow

class RTSPGameEngine(deltaTime: Float, window: RenderWindow, debug: Boolean = false)
    extends GameEngine(deltaTime, window, debug = debug) {}

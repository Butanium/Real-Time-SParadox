package rtsp

import engine2D.GameEngine
import sfml.graphics.RenderWindow
import rtsp.editor.NodeCreationMenu

class RTSPGameEngine(deltaTime: Float, window: RenderWindow, debug: Boolean = false)
    extends GameEngine(deltaTime, window, debug = debug) {
        val nodeCreationMenu : NodeCreationMenu = NodeCreationMenu(this)
        spawn(nodeCreationMenu)
        nodeCreationMenu.active = false
    }

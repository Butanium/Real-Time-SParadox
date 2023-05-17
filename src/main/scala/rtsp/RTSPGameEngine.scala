package rtsp

import engine2D.GameEngine
import sfml.graphics.RenderWindow
import rtsp.editor.NodeCreationMenu

class RTSPGameEngine(deltaTime: Float, window: RenderWindow, debug: Boolean = false)
    extends GameEngine(deltaTime, window, debug = debug) {
        val nodeCreationMenu : NodeCreationMenu = NodeCreationMenu(this)
        nodeCreationMenu.active = false
        val behaviorEditor = rtsp.editor.BehaviorEditor(this)
        behaviorEditor.zIndex = 10
        spawn(behaviorEditor)
        behaviorEditor.active = false
        behaviorEditor.add(nodeCreationMenu)
    }

package rtsp.editor

import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import engine2D.objects.RectangleObject
import rtsp.Constants
import engine2D.objects.CircleObject

class Node(engine : GameEngine) extends RectangleObject(Constants.NODE_WIDTH, Constants.NODE_HEIGHT, engine) with Grabbable(Mouse.Button.Left, engine){
    val circle = CircleObject(Constants.NODE_CIRCLE_RADIUS, engine)
    addChildren(circle)
    def whenClickedCircle() = () // TODO dessiner le trait en initialisant un nouveau Node
    listenToBoundsClicked(Mouse.Button.Left, circle, true, whenClickedCircle)
    


}

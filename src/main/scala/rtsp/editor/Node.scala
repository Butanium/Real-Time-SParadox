package rtsp.editor

import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import engine2D.objects.RectangleObject
import rtsp.Constants
import engine2D.objects.CircleObject
import engine2D.objects.LineObject
import engine2D.eventHandling.MouseEvent
import sfml.graphics.Color
import scala.collection.mutable.ListBuffer

class Node(engine: GameEngine)
    extends RectangleObject(Constants.NODE_WIDTH, Constants.NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine) {
  val square = RectangleObject(Constants.NODE_CIRCLE_RADIUS, Constants.NODE_CIRCLE_RADIUS, engine)
  square.zIndex = 10
  square.fillColor = Color.Red()
  addChildren(square)
  square.position = (Constants.NODE_WIDTH / 2f - Constants.NODE_CIRCLE_RADIUS/2f, Constants.NODE_HEIGHT)
  val follower = new GameObject(engine)
  val childrenNode : ListBuffer [Node] = ListBuffer.empty
  def release(line :LineObject): Unit =
    val childNode = new Node(engine)
    childrenNode.addOne(childNode)
    childNode.position = engine.mouseManager.mouseState.worldPos
    engine.spawn(childNode)
    line.target2 = childNode
    line.addPos2 = (Constants.NODE_WIDTH / 2f, 0f)
  def whenClickedCircle() =
    val line = LineObject(Constants.LINE_THICKNESS, this, follower, (Constants.NODE_WIDTH / 2f, Constants.NODE_HEIGHT + Constants.NODE_CIRCLE_RADIUS / 2f), (0f,0f), engine)
    engine.spawn(line)
    listenToMouseEvent(MouseEvent.ButtonReleased(Mouse.Button.Left, false), () => release(line))
  listenToMouseEvent(
    MouseEvent.BoundsPressed(square, Mouse.Button.Left, true),
    whenClickedCircle
  )
  override protected def onUpdate(): Unit =
    follower.position = engine.mouseManager.mouseState.worldPos
    super.onUpdate()

}

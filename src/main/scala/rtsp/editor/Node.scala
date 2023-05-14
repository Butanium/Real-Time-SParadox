package rtsp.editor

import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import engine2D.objects.RectangleObject
import rtsp.Constants
import engine2D.objects.CircleObject
import rtsp.editor.LineObject
import engine2D.eventHandling.MouseEvent
import sfml.graphics.Color
import scala.collection.mutable.ListBuffer
import sfml.system.Vector2

class Node(engine: GameEngine)
    extends RectangleObject(Constants.NODE_WIDTH, Constants.NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine) {
  Node.nodeList += this
  val square = RectangleObject(
    Constants.NODE_CIRCLE_RADIUS,
    Constants.NODE_CIRCLE_RADIUS,
    engine
  )
  square.zIndex = 10
  square.fillColor = Color.Red()
  addChildren(square)
  square.position = (
    Constants.NODE_WIDTH / 2f - Constants.NODE_CIRCLE_RADIUS / 2f,
    Constants.NODE_HEIGHT
  )
  val follower = new GameObject(engine)
  val childrenNode: ListBuffer[Node] = ListBuffer.empty
  val parentsNode: ListBuffer[Node] = ListBuffer.empty
  val linesLinked: ListBuffer[LineObject] = ListBuffer.empty
  def release(line: LineObject): Unit =
    val childNode =
      Node.searchNode(engine.mouseManager.mouseState.worldPos) match
        case Some(node) => node
        case None =>
          val n = Node(engine)
          n.position = engine.mouseManager.mouseState.worldPos
          n
    childrenNode.addOne(childNode)
    childNode.parentsNode.addOne(this)
    engine.spawn(childNode)
    line.target2 = childNode
    line.addPos2 = (Constants.NODE_WIDTH / 2f, 0f)
    childNode.linesLinked += line
  def whenClickedCircle() =
    val line = LineObject(
      Constants.LINE_THICKNESS,
      this,
      follower,
      (
        Constants.NODE_WIDTH / 2f,
        Constants.NODE_HEIGHT + Constants.NODE_CIRCLE_RADIUS / 2f
      ),
      (0f, 0f),
      engine
    )
    linesLinked += line
    engine.spawn(line)
    listenToMouseEvent(
      MouseEvent.ButtonReleased(Mouse.Button.Left, false),
      () => release(line)
    )
  listenToMouseEvent(
    MouseEvent.BoundsPressed(square, Mouse.Button.Left, true),
    whenClickedCircle
  )
  def delete() =
    parentsNode.foreach((node: Node) => node.childrenNode -= this)
    childrenNode.foreach((node: Node) => node.parentsNode -= this)
    linesLinked.foreach((line: LineObject) => line.deleteWithout(this))
    this.markForDeletion()

  listenToBoundsClicked(Mouse.Button.Right, this, false, delete)

  
  override protected def onUpdate(): Unit =
    follower.position = engine.mouseManager.mouseState.worldPos
    super.onUpdate()

  override protected def onDeletion(): Unit =
    Node.nodeList -= this
    super.onDeletion()

}

object Node {
  val nodeList: ListBuffer[Node] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: Node) =
    node.contains(point)
  def searchNode(point: Vector2[Float]) =
    nodeList.find((node: Node) => isInNode(point, node))

}

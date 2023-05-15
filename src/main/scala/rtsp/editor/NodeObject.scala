package rtsp.editor

import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import engine2D.objects.RectangleObject
import rtsp.Constants.EditorC.*
import engine2D.objects.CircleObject
import engine2D.objects.LineObject
import engine2D.eventHandling.MouseEvent
import sfml.graphics.Color
import scala.collection.mutable.ListBuffer
import rtsp.battle.BehaviorTree
import rtsp.RTSPGameEngine

enum NodeType:
  case Node
  case Condition
  case Filter
  case Action
  case Root

enum ActionType:
  case Move
  case Attack
  case Flee
  case Idle

class NodeObject(
    nodeType: NodeType,
    behavior: BehaviorTree,
    engine: RTSPGameEngine
) extends RectangleObject(NODE_WIDTH, NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine) {
  Node.nodeList += this
  val square = RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
  square.zIndex = 10
  square.fillColor = Color.Red()
  addChildren(square)
  square.position = (NODE_WIDTH / 2f - NODE_CIRCLE_RADIUS / 2f, NODE_HEIGHT)
  val follower = new GameObject(engine) // todo remove from here
  val childrenNode: ListBuffer[NodeObject] = ListBuffer.empty
  def release(line: LineObject): Unit =
    Node.searchNode(engine.mouseManager.mouseState.worldPos) match
      case Some(childNode) =>
        childrenNode.addOne(childNode)
        childNode.parentsNode.addOne(this)
        childNode.linesLinked += line
        line.target2 = childNode
        line.addPos2 = (Constants.NODE_WIDTH / 2f, 0f)
      case None => engine.nodeCreationMenu.createNode(this, line)

  def whenClickedCircle() =
    val line = LineObject(
      LINE_THICKNESS,
      this,
      follower,
      (NODE_WIDTH / 2f, NODE_HEIGHT + NODE_CIRCLE_RADIUS / 2f),
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
    NodeObject.nodeList -= this
    super.onDeletion()

}

object NodeObject {
  val nodeList: ListBuffer[Node] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: Node) =
    node.contains(point)
  def searchNode(point: Vector2[Float]) =
    nodeList.find((node: Node) => isInNode(point, node))

}

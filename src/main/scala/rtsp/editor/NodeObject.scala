package rtsp.editor

import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import engine2D.GameEngine
import sfml.window.Mouse
import engine2D.objects.RectangleObject
import rtsp.Constants.EditorC.*
import engine2D.objects.CircleObject
import engine2D.eventHandling.MouseEvent
import sfml.graphics.Color
import scala.collection.mutable.ListBuffer
import rtsp.battle.BehaviorTree
import rtsp.RTSPGameEngine
import sfml.system.Vector2

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
    var nodeType: NodeType,
    behavior: BehaviorTree,
    engine: RTSPGameEngine
) extends RectangleObject(NODE_WIDTH, NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine) {
  NodeObject.nodeList += this
  val square = RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
  square.zIndex = 10
  square.fillColor = Color.Red()
  addChildren(square)
  square.position = (NODE_WIDTH / 2f - NODE_CIRCLE_RADIUS / 2f, NODE_HEIGHT)
  val follower = new GameObject(engine) // todo remove from here
  val childrenNode: ListBuffer[NodeObject] = ListBuffer.empty
  val parentsNode: ListBuffer[NodeObject] = ListBuffer.empty
  val linesLinked: ListBuffer[LineObject] = ListBuffer.empty
  def release(line: LineObject): Unit =
    NodeObject.searchNode(engine.mouseManager.mouseState.worldPos) match
      case Some(childNode) =>
        childrenNode.addOne(childNode)
        childNode.parentsNode.addOne(this)
        childNode.linesLinked += line
        line.target2 = childNode
        line.addPos2 = (NODE_WIDTH / 2f, 0f)
      case None => engine.nodeCreationMenu.createNode(this, line)

  def whenSquareClicked() =
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
    follower.active = true
    listenToMouseEvent(
      MouseEvent.ButtonReleased(Mouse.Button.Left, false),
      () => {
        release(line)
        follower.active = false
      }
    )
  listenToMouseEvent(
    MouseEvent.BoundsPressed(square, Mouse.Button.Left, true),
    whenSquareClicked
  )
  def delete() =
    parentsNode.foreach((node: NodeObject) => node.childrenNode -= this)
    childrenNode.foreach((node: NodeObject) => node.parentsNode -= this)
    linesLinked.foreach((line: LineObject) => line.deleteWithout(this))
    this.markForDeletion()

  listenToBoundsClicked(Mouse.Button.Right, this, false, delete)

  override protected def onUpdate(): Unit =
    if follower.active then
      follower.position = engine.mouseManager.mouseState.worldPos
    super.onUpdate()

  override protected def onDeletion(): Unit =
    NodeObject.nodeList -= this
    super.onDeletion()

}

object NodeObject {
  val nodeList: ListBuffer[NodeObject] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: NodeObject) =
    node.nodeType != NodeType.Root && node.contains(point)
  def searchNode(point: Vector2[Float]): Option[NodeObject] =
    nodeList.find(node => isInNode(point, node))

}

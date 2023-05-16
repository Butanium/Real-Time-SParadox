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
import engine2D.objects.TextObject
import rtsp.battle.Behavior


class CyclicDependencyException extends Exception("Cyclic dependency detected")

enum NodeType:
  case Node
  case Condition
  case Filter // rien en dessous
  case Action // modification du truc en dessous
  case Root // rien au dessus

enum ActionType:
  case Move
  case Attack
  case Flee
  case Idle

enum ConversionState:
  case Done
  case Doing
  case New

class NodeObject(
    var nodeType: NodeType,
    behavior: BehaviorTree,
    engine: RTSPGameEngine
) extends RectangleObject(NODE_WIDTH, NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine) {
  import NodeType.*
  // ajout du NodeObject à la liste des NodeObjects
  var conversionState = ConversionState.New // Is used to avoid cyclic behavior tree
  NodeObject.nodeList += this

  // Création d'un GameObject qui suit la souris
  val follower = new GameObject(engine) // todo remove from here

  // Initialisation des listes associées au NodeObject
  val childrenNode: ListBuffer[NodeObject] = ListBuffer.empty
  val parentsNode: ListBuffer[NodeObject] = ListBuffer.empty
  val linesLinked: ListBuffer[LineObject] = ListBuffer.empty

  // définition des booléens associés au NodeObject
  def canHaveChild: Boolean = nodeType match
    case Node => true
    case Condition => true
    case Filter => false
    case Action => true
    case Root => true
  def canHaveParent: Boolean = nodeType match
    case Node => true
    case Condition => true
    case Filter => true
    case Action => true
    case Root => false

  if canHaveChild then
    val square = RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
    square.zIndex = 10
    square.fillColor = Color.Red()
    addChildren(square)
    square.position = (NODE_WIDTH / 2f - NODE_CIRCLE_RADIUS / 2f, NODE_HEIGHT)
    def release(line: LineObject): Unit =
      NodeObject.searchNode(engine.mouseManager.mouseState.worldPos, this) match
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
  def release(line: LineObject): Unit =
    NodeObject.searchNode(engine.mouseManager.mouseState.worldPos, this) match
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
      MouseEvent.BoundsPressed(square, Mouse.Button.Left, true),
      whenSquareClicked
    )
  // Définition du carré au-dessus du NodeObject quand nécessaire
  if canHaveParent then
    val squareAbove = RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
    squareAbove.zIndex = 10
    squareAbove.fillColor = Color.Red()
    addChildren(squareAbove)
    squareAbove.position = (NODE_WIDTH / 2f - NODE_CIRCLE_RADIUS / 2f, -(NODE_CIRCLE_RADIUS / 2f))

  //Définition du texte écrit sur le NodeObject en fonction de son type
  val textNodeType: String = nodeType match
    case Node => "Node"
    case Condition => "Condition"
    case Filter => "Filter"
    case Action => "Action"
    case Root => "Root"
  val textType = new TextObject(textNodeType, engine, charSize = 16)
  textType.fillColor = Color.Red()
  addChildren(textType)
  textType.position =
    (NODE_WIDTH / 10f, NODE_HEIGHT / 3f)
  
  // Supression du NodeObject quand on fait click droit dessus
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

  def toBehaviorTree : BehaviorTree = 
    if this.conversionState == ConversionState.Done then
      this.behavior
    else if this.conversionState == ConversionState.Doing then
      throw CyclicDependencyException()
    else
      this.conversionState = ConversionState.Doing
      val behavior = this.nodeType match
        case NodeType.Action =>
          val action = this.behavior match
            case BehaviorTree.ActionNode(action, _) => action
            case _ => throw new Exception("Wrong behavior type")
          BehaviorTree.ActionNode(action, this.parentsNode.map(_.toBehaviorTree).toList)
        case NodeType.Condition =>
          val condition = this.behavior match
            case BehaviorTree.ConditionNode(condition, _, _) => condition
            case _ => throw new Exception("Wrong behavior type")
          BehaviorTree.ConditionNode(condition, this.parentsNode.map(_.toBehaviorTree).toList, this.childrenNode.map(_.toBehaviorTree).toList)
        case NodeType.Filter =>
          val filter = this.behavior match
            case BehaviorTree.Node(children, _) => children
            case _ => throw new Exception("Wrong behavior type")
          BehaviorTree.Node(filter, this.parentsNode.map(_.toBehaviorTree).toList)
        case NodeType.Node =>
          val children = this.behavior match
            case BehaviorTree.Node(children, _) => children
            case _ => throw new Exception("Wrong behavior type")
          BehaviorTree.Node(children, this.parentsNode.map(_.toBehaviorTree).toList)
        case NodeType.Root =>
          val children = this.behavior match
            case BehaviorTree.Node(children, _) => children
            case _ => throw new Exception("Wrong behavior type")
          BehaviorTree.Node(children, this.parentsNode.map(_.toBehaviorTree).toList)
    
    


}

object NodeObject {
  val nodeList: ListBuffer[NodeObject] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: NodeObject) =
    node.nodeType != NodeType.Root && node.contains(point)
  def searchNode(point: Vector2[Float], from: NodeObject): Option[NodeObject] =
    nodeList
      .sortBy(-_.zIndex)
      .find(node => node != from && isInNode(point, node))

  def fromBehavior(
      behavior: BehaviorTree,
      parent: GameObject,
      engine: RTSPGameEngine
  ): NodeObject =
    val n = behavior match
      case BehaviorTree.ActionNode(action, _) =>
        NodeObject(NodeType.Action, behavior, engine)
      case BehaviorTree.Node(children, _) =>
        val childrenNodes = children.map(fromBehavior(_, parent, engine))
        val n = NodeObject(NodeType.Node, behavior, engine)
        n.childrenNode.addAll(childrenNodes)
        n
      case BehaviorTree.ConditionNode(condition, children, _) =>
        val childrenNodes = children.map(fromBehavior(_, parent, engine))
        val n = NodeObject(NodeType.Condition, behavior, engine)
        n.childrenNode.addAll(childrenNodes)
        n
    n.position = behavior.position
    parent.addChildren(n)
    n
}

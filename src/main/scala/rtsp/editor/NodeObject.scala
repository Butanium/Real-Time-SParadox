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
import engine2D.objects.OnHover
import engine2D.objects.ButtonObject
import rtsp.battle.Filter
import rtsp.battle.Condition

class CyclicDependencyException extends Exception("Cyclic dependency detected")

enum NodeType:
  case Node
  case Condition
  case Filter // rien en dessous
  case Action // modification du truc en dessous
  case Root // rien au dessus

enum ConversionState:
  case Done
  case Doing
  case New

/** In our behavior tree, filters aren't nodes, but we need to represent them
  * graphically. This enum is used be able to pass a filter to the NodeObject
  */
enum BehaviorNode:
  case BNode(node: BehaviorTree)
  case FilterNode(filter: Filter)
  def isFilter: Boolean = this match
    case FilterNode(_) => true
    case _             => false
  override def toString: String =
    this match
      case BNode(node)        => node.toString
      case FilterNode(filter) => filter.toString

/** A NodeObject is a graphical representation of a node in a behavior tree. It
  * is used in the behavior editor.
  * @param nodeType
  *   The type of the node
  * @param behavior
  *   The behavior tree associated to the node
  * @param engine
  *   The game engine
  */
class NodeObject(
    var nodeType: NodeType,
    behavior: BehaviorNode,
    engine: RTSPGameEngine
) extends RectangleObject(NODE_WIDTH, NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine)
    with OnHover {
  // Tooltip text displayed when hovering the node
  val tooltip = ButtonObject(behavior.toString, () => (), engine)
  tooltip.zIndex = 4
  engine.behaviorEditor.add(tooltip)
  // The tooltip is at the bottom of the window
  tooltip.position = (0f, engine.window.size.y - tooltip.height - 30)
  initShowOnHover(tooltip, this)

  this.outlineColor = Color(150, 150, 150)
  this.outlineThickness = 6f

  // ajout du NodeObject à la liste des NodeObjects
  var conversionState =
    ConversionState.New // Is used to avoid cyclic behavior tree

  NodeObject.nodeList += this

  // Création d'un GameObject qui suit la souris
  val follower = new GameObject(engine) // todo remove from here

  // Initialisation des listes associées au NodeObject
  val childrenNode: ListBuffer[NodeObject] = ListBuffer.empty
  val parentsNode: ListBuffer[NodeObject] = ListBuffer.empty
  val linesLinked: ListBuffer[LineObject] = ListBuffer.empty

  // définition des booléens associés au NodeObject
  def canHaveChild: Boolean = nodeType match
    case NodeType.Filter => false
    case _               => true

  def canHaveParent: Boolean = nodeType match
    case NodeType.Root => false
    case _    => true

  if canHaveChild then
    val square = RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
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
      engine.behaviorEditor.add(line)
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
  def release(line: LineObject): Unit =
    NodeObject.searchNode(engine.mouseManager.mouseState.worldPos, this) match
      case Some(childNode) =>
        childrenNode.addOne(childNode)
        childNode.parentsNode.addOne(this)
        childNode.linesLinked += line
        line.target2 = childNode
        line.addPos2 = (NODE_WIDTH / 2f, 0f)
      case None => engine.nodeCreationMenu.createNode(this, line)
  // Définition du carré au-dessus du NodeObject quand nécessaire
  if canHaveParent then
    val squareAbove =
      RectangleObject(NODE_CIRCLE_RADIUS, NODE_CIRCLE_RADIUS, engine)
    squareAbove.fillColor = Color(255, 150, 0, 255)
    addChildren(squareAbove)
    squareAbove.position =
      (NODE_WIDTH / 2f - NODE_CIRCLE_RADIUS / 2f, -(NODE_CIRCLE_RADIUS / 2f))

  // Définition du texte écrit sur le NodeObject en fonction de son type
  val textNodeType: String = nodeType.toString
  val textType = new TextObject(textNodeType, engine, charSize = 16)
  textType.fillColor = Color.Red()
  addChildren(textType)
  textType.position = (NODE_WIDTH / 10f, NODE_HEIGHT / 3f)
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
    tooltip.changeText(behavior.toString, adaptBackground = true)
    tooltip.position = (0f, engine.window.size.y - tooltip.height - 30)
    super.onUpdate()

  override protected def onDeletion(): Unit =
    NodeObject.nodeList -= this
    super.onDeletion()

  override def markForDeletion(): Unit =
    linesLinked.foreach(_.markForDeletion())
    tooltip.markForDeletion()
    super.markForDeletion()

  def toFilter: Filter =
    behavior match
      case BehaviorNode.FilterNode(filter) => filter
      case _ => throw Exception("BNode should not be converted to Filter")

  def toCondition(condition: Condition): Condition = {
    condition match
      case Condition.Count(target, filter, countCondition) =>
        Condition.Count(
          target,
          childrenNode
            .filter(_.nodeType == NodeType.Filter)
            .map(_.toFilter)
            .toList,
          countCondition
        )
      case Condition.Not(condition) => Condition.Not(toCondition(condition))
  }

  def toBehaviorTree: BehaviorTree =
    behavior match
      case BehaviorNode.FilterNode(filter) =>
        throw Exception("FilterNode should not be converted to BehaviorTree")
      case BehaviorNode.BNode(behavior) =>
        if this.conversionState == ConversionState.Done then behavior
        else if this.conversionState == ConversionState.Doing then
          throw CyclicDependencyException()
        else
          this.conversionState = ConversionState.Doing
          val nodeBehavior = behavior match
            case BehaviorTree.ActionNode(action, _) =>
              action.filters = childrenNode
                .filter(_.nodeType == NodeType.Filter)
                .map(_.toFilter)
                .toList
              BehaviorTree.ActionNode(action, position)
            case rtsp.battle.BehaviorTree.Node(_, _) =>
              BehaviorTree.Node(
                childrenNode
                  .filter(_.nodeType != NodeType.Filter)
                  .sortBy(_.position.x)
                  .map(_.toBehaviorTree)
                  .toList,
                position
              )
            case BehaviorTree.ConditionNode(condition, _, _) =>
              BehaviorTree.ConditionNode(
                condition,
                childrenNode
                  .filter(_.nodeType != NodeType.Filter)
                  .sortBy(_.position.x)
                  .map(_.toBehaviorTree)
                  .toList,
                position
              )
          conversionState = ConversionState.Done
          nodeBehavior

  /** Créer un lien entre deux NodeObject
    */
  def linkTo(node: NodeObject): Unit =
    val line = LineObject(
      LINE_THICKNESS,
      this,
      node,
      (NODE_WIDTH / 2f, NODE_HEIGHT + NODE_CIRCLE_RADIUS / 2f),
      (NODE_WIDTH / 2f, 0f),
      engine
    )
    linesLinked += line
    node.linesLinked += line
    engine.behaviorEditor.add(line)
    childrenNode.addOne(node)
    node.parentsNode.addOne(this)

}

object NodeObject {
  import BehaviorNode.*
  val nodeList: ListBuffer[NodeObject] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: NodeObject) =
    node.nodeType != NodeType.Root && node.contains(point)
  def searchNode(point: Vector2[Float], from: NodeObject): Option[NodeObject] =
    nodeList
      .sortBy(_.inverseOrder)
      .find(node => node != from && isInNode(point, node))

  def fromFilter(
      filter: Filter,
      parent: GameObject,
      engine: RTSPGameEngine
  ): NodeObject =
    val n = NodeObject(NodeType.Filter, BehaviorNode.FilterNode(filter), engine)
    n.position = filter.position
    parent.add(n)
    n

  def fromBehavior(
      behavior: BehaviorTree,
      parent: GameObject,
      engine: RTSPGameEngine,
      isRoot: Boolean = false
  ): NodeObject =
    val n = behavior match
      case BehaviorTree.ActionNode(action, _) =>
        val childrenNodes = action.filters.map(fromFilter(_, parent, engine))
        if isRoot then throw Exception("Root node cannot be an action node")
        val n = NodeObject(NodeType.Action, BNode(behavior), engine)
        childrenNodes.foreach(n.linkTo(_))
        n
      case BehaviorTree.Node(children, _) =>
        val childrenNodes = children.map(fromBehavior(_, parent, engine))
        val n = NodeObject(
          if isRoot then NodeType.Root else NodeType.Node,
          BNode(behavior),
          engine
        )
        childrenNodes.foreach(n.linkTo(_))
        n
      case BehaviorTree.ConditionNode(condition, children, _) =>
        val childrenNodes = 
          children.map(fromBehavior(_, parent, engine))
          ++
          condition.filters.map(fromFilter(_, parent, engine))
        if isRoot then throw Exception("Root node cannot be an action node")
        val n = NodeObject(NodeType.Condition,
          BNode(behavior),
          engine
        )
        childrenNodes.foreach(n.linkTo(_))
        n
    n.position = behavior.position
    parent.addChildren(n)
    n
}

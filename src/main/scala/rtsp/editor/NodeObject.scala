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
import rtsp.battle.behaviorString
import engine2D.objects.ButtonObject

class CyclicDependencyException extends Exception("Cyclic dependency detected")

enum NodeType:
  case Node
  case Condition
  case Filter
  case Action
  case Root

enum ConversionState:
  case Done
  case Doing
  case New

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
    behavior: BehaviorTree,
    engine: RTSPGameEngine
) extends RectangleObject(NODE_WIDTH, NODE_HEIGHT, engine)
    with Grabbable(Mouse.Button.Left, engine)
    with OnHover {
  // Tooltip text displayed when hovering the node
  val tooltip = ButtonObject(behaviorString(behavior), () => (), engine)
  tooltip.zIndex = 4
  engine.behaviorEditor.add(tooltip)
  // The tooltip is at the bottom of the window
  tooltip.position = (0f, engine.window.size.y - tooltip.height - 30)
  initShowOnHover(tooltip, this)

  this.outlineColor = Color(150, 150, 150)
  this.outlineThickness = 6f
  import NodeType.*
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
    case Filter => false
    case _      => true

  def canHaveParent: Boolean = nodeType match
    case Root => false
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
  val textNodeType: String = nodeType match
    case Node      => "Node"
    case Condition => "Condition"
    case Filter    => "Filter"
    case Action    => "Action"
    case Root      => "Root"
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
    tooltip.changeText(behaviorString(behavior), adaptBackground = true)
    tooltip.position = (0f, engine.window.size.y - tooltip.height - 30)
    super.onUpdate()

  override protected def onDeletion(): Unit =
    NodeObject.nodeList -= this
    super.onDeletion()

  override def markForDeletion(): Unit =
    linesLinked.foreach(_.markForDeletion())
    tooltip.markForDeletion()
    super.markForDeletion()

  def toBehaviorTree: BehaviorTree =
    if this.conversionState == ConversionState.Done then this.behavior
    else if this.conversionState == ConversionState.Doing then
      throw CyclicDependencyException()
    else
      this.conversionState = ConversionState.Doing
      val behavior = this.behavior match
        case BehaviorTree.ActionNode(action, _) =>
          BehaviorTree.ActionNode(action, position)
        case rtsp.battle.BehaviorTree.Node(_, _) =>
          BehaviorTree.Node(childrenNode.sortBy(_.position.x).map(_.toBehaviorTree).toList, position)
        case BehaviorTree.ConditionNode(condition, _, _) =>
          BehaviorTree.ConditionNode(
            condition,
            childrenNode.sortBy(_.position.x).map(_.toBehaviorTree).toList,
            position
          )
      conversionState = ConversionState.Done
      behavior

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
  val nodeList: ListBuffer[NodeObject] = ListBuffer.empty
  def isInNode(point: Vector2[Float], node: NodeObject) =
    node.nodeType != NodeType.Root && node.contains(point)
  def searchNode(point: Vector2[Float], from: NodeObject): Option[NodeObject] =
    nodeList
      .sortBy(_.inverseOrder)
      .find(node => node != from && isInNode(point, node))

  def fromBehavior(
      behavior: BehaviorTree,
      parent: GameObject,
      engine: RTSPGameEngine,
      isRoot: Boolean = false
  ): NodeObject =
    val n = behavior match
      case BehaviorTree.ActionNode(action, _) =>
        if isRoot then throw Exception("Root node cannot be an action node")
        NodeObject(NodeType.Action, behavior, engine)
      case BehaviorTree.Node(children, _) =>
        val childrenNodes = children.map(fromBehavior(_, parent, engine))
        val n = NodeObject(
          if isRoot then NodeType.Root else NodeType.Node,
          behavior,
          engine
        )
        childrenNodes.foreach(n.linkTo(_))
        n
      case BehaviorTree.ConditionNode(condition, children, _) =>
        val childrenNodes = children.map(fromBehavior(_, parent, engine))
        val n = NodeObject(
          if isRoot then NodeType.Root else NodeType.Condition,
          behavior,
          engine
        )
        childrenNodes.foreach(n.linkTo(_))
        n
    n.position = behavior.position
    parent.addChildren(n)
    n
}

package rtsp.editor

import engine2D.GameEngine
import rtsp.battle.Behavior
import rtsp.battle.Target
import rtsp.battle.Metric
import rtsp.Constants.EditorC.NODE_WIDTH
import rtsp.battle.BehaviorTree
import rtsp.battle.CountCondition
import rtsp.battle.Team
import rtsp.battle.Condition
import rtsp.battle.Action
import rtsp.battle.Selector
import rtsp.RTSPGameEngine
import engine2D.objects.ButtonObject
import engine2D.objects.GameObject

class NodeCreationMenu(engine: RTSPGameEngine) extends GameObject(engine) {
  zIndex = 10
  var currentNode: NodeObject = null
  var nodeType: NodeType = NodeType.Action
  var actionType: ActionType = ActionType.Attack
  var target: Target = Target.Warrior(Team.Enemy)
  var metric: Metric = Metric.DistanceFromClosest(target)
  var currentLine: LineObject = null
  var selector: Selector = Selector.Lowest(metric)
  var notCondition = false
  var countCondition: CountCondition = CountCondition.Equals(0)
  def createNode(parentNode: NodeObject, line: LineObject) =
    active = true
    currentNode = parentNode
    currentLine = line
    nodeTypeMenu.open()

  def computeNode(): NodeObject =
    import BehaviorTree.*
    val behavior: BehaviorTree = nodeType match
      case NodeType.Node => Node(List.empty)
      case NodeType.Condition => {
        var condition = Condition.Count(target, List.empty, countCondition)
        if (notCondition) condition = Condition.Not(condition)
        ConditionNode(condition, List.empty)
      }
      case NodeType.Filter => throw new Exception("Not implemented")
      case NodeType.Action =>
        ActionNode(
          actionType match
            case ActionType.Move => Action.Move(target, List.empty, selector)
            case ActionType.Attack =>
              Action.Attack(target, List.empty, selector)
            case ActionType.Flee => Action.Flee(target, List.empty, selector)
            case ActionType.Idle => Action.Idle(List.empty),
        )
      case _ => throw new Exception("Unreachable Node Type")
    NodeObject(nodeType, behavior, engine)

  def onClose(): Unit =
    this.active = false
    children.foreach(_.active = false)
    doneButton.active = true
    val childNode = computeNode()
    currentNode.childrenNode.addOne(childNode)
    childNode.parentsNode.addOne(currentNode)
    childNode.linesLinked += currentLine
    childNode.position = currentNode.follower.position
    engine.spawn(childNode)
    currentLine.target2 = childNode
    currentLine.addPos2 = (NODE_WIDTH / 2f, 0f)
  val doneButton =
    ButtonObject("Done", onClose, engine)
  doneButton.zIndex = 3
  doneButton.position = (engine.window.size.x - doneButton.background.width, 0f)
  addChildren(doneButton)

  /* ------ Node Type Menu ------ */
  def onClickedNode() = {
    nodeType = NodeType.Node
  }
  def onClickedCondition() = {
    // nodeType = NodeType.Condition
    // conditionMenu.open()
  }
  def onClickedFilter() = {}
  def onClickedAction(): Unit = {
    nodeType = NodeType.Action
    actionTypeMenu.open()
  }
  val nodeButton = ButtonObject("Node", onClickedNode, engine)
  val conditionButton =
    ButtonObject("Condition (not Implemented)", onClickedCondition, engine)
  val filterButton =
    ButtonObject("Filter (not implemented)", onClickedFilter, engine)
  val actionButton = ButtonObject("Action", onClickedAction, engine)
  val typeButtons =
    List(nodeButton, actionButton, conditionButton, filterButton)
  val nodeTypeMenu = MultipleChoiceMenu(typeButtons, None, true, engine)
  nodeTypeMenu.onClose = () => {
    currentLine.markForDeletion()
    currentLine.deletionSquare.markForDeletion()
    active = false
    // disable submenus
    children.foreach(_.active = false)
    doneButton.active = true
  }
  addChildren(nodeTypeMenu)

  /* ------ Action Type Menu ------ */
  def onClickedMove() = {
    actionType = ActionType.Move
    actionMenu.open()
  }
  def onClickedAttack() = {
    actionType = ActionType.Attack
    actionMenu.open()
  }
  def onClickedFlee() = {
    actionType = ActionType.Flee
    actionMenu.open()
  }
  def onClickedIdle() = {
    actionType = ActionType.Idle
  }
  val moveButton = ButtonObject("Move", onClickedMove, engine)
  val attackButton = ButtonObject("Attack", onClickedAttack, engine)
  val fleeButton = ButtonObject("Flee", onClickedFlee, engine)
  val idleButton = ButtonObject("Idle", onClickedIdle, engine)
  val actionButtons = List(moveButton, attackButton, fleeButton, idleButton)
  val actionTypeMenu: MultipleChoiceMenu =
    MultipleChoiceMenu(actionButtons, Some(nodeTypeMenu), true, engine)
  addChildren(actionTypeMenu)

  /* ----- Action Menu ----- */
  val actionTargetMenu = makeTargetMenu
  def onClickedTarget(): Unit = {
    actionTargetMenu.open()
  }
  def onClickedSelector(): Unit = {
    selectorMenu.open()
  }
  val targetButton = ButtonObject("Target", onClickedTarget, engine)
  val selectorButton = ButtonObject("Selector", onClickedSelector, engine)
  val actionMenu = MultipleChoiceMenu(
    List(targetButton, selectorButton),
    Some(actionTypeMenu),
    false,
    engine
  )
  addChildren(actionMenu)
  actionTargetMenu.uiParent = Some(actionMenu)

  /* ----- Target Menu ----- */
  def makeTargetMenu: MultipleChoiceMenu =
    val teamMenu = makeTeamMenu
    def onClickedWarrior = () => {
      target = Target.Warrior(target.team)
      teamMenu.open()
    }
    def onClickedBase = () => {
      target = Target.Base(target.team)
      teamMenu.open()
    }
    val warriorButton = ButtonObject("Warrior", onClickedWarrior, engine)
    val baseButton = ButtonObject("Base", onClickedBase, engine)
    val targetMenu: MultipleChoiceMenu =
      MultipleChoiceMenu(
        List(warriorButton, baseButton),
        None, // needs to be set by parent
        true,
        engine
      )
    teamMenu.uiParent = Some(targetMenu)
    addChildren(targetMenu)
    targetMenu

  /* ----- Team Menu ----- */
  def makeTeamMenu: MultipleChoiceMenu =
    def onClickedAlly = () => {
      target.team = Team.Ally
    }
    def onClickedEnemy = () => {
      target.team = Team.Enemy
    }
    val allyButton = ButtonObject("Ally", onClickedAlly, engine)
    val enemyButton = ButtonObject("Enemy", onClickedEnemy, engine)
    val teamMenu =
      MultipleChoiceMenu(
        List(allyButton, enemyButton),
        None, // needs to be set by parent
        true,
        engine
      )
    addChildren(teamMenu)
    teamMenu

  /* ----- Selector Menu ----- */
  val selectorMetricMenu = makeMetricMenu
  def onClickedLowest(): Unit = {
    selector = Selector.Lowest(metric)
    selectorMetricMenu.open()
  }
  def onClickedHighest(): Unit = {
    selector = Selector.Highest(metric)
    selectorMetricMenu.open()
  }
  val lowestButton = ButtonObject("Lowest", onClickedLowest, engine)
  val highestButton = ButtonObject("Highest", onClickedHighest, engine)
  val selectorMenu =
    MultipleChoiceMenu(
      List(lowestButton, highestButton),
      Some(actionTypeMenu),
      true,
      engine
    )
  selectorMetricMenu.uiParent = Some(selectorMenu)
  addChildren(selectorMenu)

  /* ----- Metric Menu ----- */
  def makeMetricMenu: MultipleChoiceMenu =
    val targetMenu = makeTargetMenu
    def onClickedDistance(): Unit = {
      metric = Metric.DistanceFromClosest(target)
      targetMenu.open()
    }
    def onClickedHealth(): Unit = {
      metric = Metric.Health
    }
    def onClickedHealthPercentage(): Unit = {
      metric = Metric.HealthPercentage
    }
    val distanceButton = ButtonObject("Distance", onClickedDistance, engine)
    val healthButton = ButtonObject("Health", onClickedHealth, engine)
    val healthPercentageButton =
      ButtonObject("Health Percentage", onClickedHealthPercentage, engine)
    val metricMenu = MultipleChoiceMenu(
      List(distanceButton, healthButton, healthPercentageButton),
      None, // Needs to be edited upstream
      true,
      engine
    )
    targetMenu.uiParent = Some(metricMenu)
    addChildren(metricMenu)
    metricMenu
}

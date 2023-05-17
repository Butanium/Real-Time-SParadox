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
import rtsp.battle.Filter

enum ActionType:
  case Move
  case Attack
  case Flee
  case Idle

enum FilterType:
  case LessThan
  case GreaterThan
  case Equals
  case Attacking
  case AttackedBy
  case MovingTo
  case ApproachedBy
  case FleeingFrom
  case FledBy
  case CanAttack
  case CanBeAttackedBy
  case Idling

enum CompType:
  case Equals
  case GreaterThan
  case LessThan

enum TargetType:
  case Warrior
  case Base
  case Self

enum MetricType:
  case DistanceFromClosest
  case Health
  case HealthPercentage

enum SelectorType:
  case Highest
  case Lowest

trait Value {
  var value: Float = 0
  var negation: Boolean = false
  var compType: CompType = CompType.LessThan
}

class ValueFactory extends Value {}

/** Allows to edit the different components of a Target independently */
class TargetFactory {
  var targetType = TargetType.Warrior
  var team = Team.Enemy
  def toTarget = targetType match
    case TargetType.Warrior => Target.Warrior(team)
    case TargetType.Base    => Target.Base(team)
    case TargetType.Self    => Target.Self
}

/** Allows to edit the different components of a Metric independently */
class MetricFactory extends TargetFactory with Value {
  var metricType = MetricType.DistanceFromClosest
  def toMetric = metricType match
    case MetricType.DistanceFromClosest => Metric.DistanceFromClosest(toTarget)
    case MetricType.Health              => Metric.Health
    case MetricType.HealthPercentage    => Metric.HealthPercentage
}

/** Contains all the submenus that can be opened when creating a new node. This
  * is in one file to avoid repetition and handle circular dependencies
  */
class NodeCreationMenu(engine: RTSPGameEngine) extends GameObject(engine) {
  zIndex = 10
  var currentLine: LineObject = null
  var currentNode: NodeObject = null
  /* ------ New node characteristics ------ */
  var nodeType = NodeType.Action
  var filterType = FilterType.LessThan
  var actionType = ActionType.Attack
  var metricType = MetricType.DistanceFromClosest
  var selectorType = SelectorType.Lowest

  val actionTargetFactory = TargetFactory()
  val conditionTargetFactory = TargetFactory()
  val filterTargetFactory = TargetFactory()

  val selectorMetricFactory = MetricFactory()
  val filterMetricFactory = MetricFactory()

  var notCondition = false
  var conditionValue = ValueFactory()
  var countCondition = CompType.Equals

  /** Initialize the node creation menu */
  def createNode(parentNode: NodeObject, line: LineObject) =
    active = true
    currentNode = parentNode
    currentLine = line
    nodeTypeMenu.open()

  def makeTarget(targetType: TargetType, team: Team) = targetType match
    case TargetType.Warrior => Target.Warrior(team)
    case TargetType.Base    => Target.Base(team)
    case TargetType.Self    => Target.Self

  def makeSelector = selectorType match
    case SelectorType.Highest =>
      Selector.Highest(selectorMetricFactory.toMetric)
    case SelectorType.Lowest => Selector.Lowest(selectorMetricFactory.toMetric)

  /** Compute a NodeObject based on the characteristics above */
  def computeNode(): NodeObject =
    import BehaviorTree.*
    import BehaviorNode.*
    val behavior: BehaviorNode = nodeType match
      case NodeType.Node =>
        BNode(Node(List.empty, currentNode.follower.position))
      case NodeType.Condition => {
        val count = countCondition match {
          case CompType.Equals =>
            CountCondition.Equals(conditionValue.value.toInt)
          case CompType.GreaterThan =>
            CountCondition.GreaterThan(conditionValue.value.toInt)
          case CompType.LessThan =>
            CountCondition.LessThan(conditionValue.value.toInt)
        }
        var condition =
          Condition.Count(conditionTargetFactory.toTarget, List.empty, count)
        if (notCondition) condition = Condition.Not(condition)
        BNode(
          ConditionNode(condition, List.empty, currentNode.follower.position)
        )
      }
      case NodeType.Filter =>
        FilterNode(filterType match
          case FilterType.LessThan =>
            Filter.LessThan(
              filterMetricFactory.value,
              filterMetricFactory.toMetric,
              currentNode.follower.position
            )
          case FilterType.GreaterThan =>
            Filter.GreaterThan(
              filterMetricFactory.value,
              filterMetricFactory.toMetric,
              currentNode.follower.position
            )
          case FilterType.Equals =>
            Filter.Equals(
              filterMetricFactory.value,
              filterMetricFactory.toMetric,
              currentNode.follower.position
            )
          case FilterType.Attacking =>
            Filter.Attacking(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.AttackedBy =>
            Filter.AttackedBy(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.MovingTo =>
            Filter.MovingTo(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.ApproachedBy =>
            Filter.ApproachedBy(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.FleeingFrom =>
            Filter.FleeingFrom(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.FledBy =>
            Filter.FledBy(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.CanAttack =>
            Filter.CanAttack(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.CanBeAttackedBy =>
            Filter.CanBeAttackedBy(
              filterTargetFactory.toTarget,
              currentNode.follower.position
            )
          case FilterType.Idling =>
            Filter.Idling(currentNode.follower.position)
        )

      case NodeType.Action =>
        val target = actionTargetFactory.toTarget
        val selector = makeSelector
        BNode(
          ActionNode(
            actionType match
              case ActionType.Move =>
                Action.Move(target, List.empty, makeSelector)
              case ActionType.Attack =>
                Action.Attack(target, List.empty, makeSelector)
              case ActionType.Flee =>
                Action.Flee(target, List.empty, makeSelector)
              case ActionType.Idle => Action.Idle(List.empty)
            ,
            currentNode.follower.position
          )
        )
      case _ => throw new Exception("Unreachable Node Type")
    NodeObject(nodeType, behavior, engine)

  /** Create a new node and add it to the visual tree. Called when the user
    * clicks on the "Done" button.
    */
  def onDone(): Unit =
    this.active = false
    children.foreach(_.active = false)
    engine.behaviorEditor.menu.active = true
    doneButton.active = true
    val childNode = computeNode()
    currentNode.childrenNode.addOne(childNode)
    childNode.parentsNode.addOne(currentNode)
    childNode.linesLinked += currentLine
    childNode.position = currentNode.follower.position
    engine.behaviorEditor.add(childNode)
    currentLine.target2 = childNode
    currentLine.addPos2 = (NODE_WIDTH / 2f, 0f)
  val doneButton =
    ButtonObject("Done", onDone, engine)
  doneButton.zIndex = 3
  doneButton.position = (engine.window.size.x - doneButton.background.width, 0f)
  addChildren(doneButton)

  /* ------ Node Type Menu ------ */
  def onClickedNode() = {
    nodeType = NodeType.Node
  }
  def onClickedCondition() = {
    nodeType = NodeType.Condition
    conditionMenu.open()
  }
  def onClickedFilter() = {
    nodeType = NodeType.Filter
    filterMenu.open()
  }
  def onClickedAction(): Unit = {
    nodeType = NodeType.Action
    actionTypeMenu.open()
  }
  val nodeButton = ButtonObject("Node", onClickedNode, engine)
  val conditionButton =
    ButtonObject("Condition", onClickedCondition, engine)
  val filterButton =
    ButtonObject("Filter", onClickedFilter, engine)
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
  val actionTargetMenu = makeTargetMenu(actionTargetFactory)
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

  /* ----- Condition Menu ----- */
  val conditionTargetMenu = makeTargetMenu(conditionTargetFactory)
  val countConditionMenu = makeComparaisonMenu(conditionValue, List(1, -1))
  val notButton: ButtonObject = makeNegButton(conditionValue)
  val countConditionButton = ButtonObject(
    "Count Condition",
    () => {
      countConditionMenu.open()
    },
    engine
  )
  val conditionTargetButton = ButtonObject(
    "Target",
    () => {
      conditionTargetMenu.open()
    },
    engine
  )
  val conditionMenu: MultipleChoiceMenu = MultipleChoiceMenu(
    List(notButton, countConditionButton, conditionTargetButton),
    Some(nodeTypeMenu),
    false,
    engine
  )
  addChildren(conditionMenu)
  conditionTargetMenu.uiParent = Some(conditionMenu)
  countConditionMenu.uiParent = Some(conditionMenu)

  /* ----- Negation Button ----- */
  def makeNegButton(value: Value): ButtonObject =
    lazy val notButton: ButtonObject = ButtonObject(
      "If",
      () => {
        value.negation = !value.negation
        notButton.changeText(if value.negation then "If not" else "If")
      },
      engine
    )
    notButton

  /* ----- Filter Menu ----- */
  val passiveActionFilterButton = ButtonObject(
    "Passive\naction Filter",
    () => {
      passiveActionFilterMenu.open()
    },
    engine
  )
  val activeActionFilterButton = ButtonObject(
    "Active\naction Filter",
    () => {
      activeActionFilterMenu.open()
    },
    engine
  )
  val metricFilterButton = ButtonObject(
    "Metric Filter",
    () => {
      metricFilterMenu.open()
    },
    engine
  )
  val filterMenu: MultipleChoiceMenu = MultipleChoiceMenu(
    List(
      passiveActionFilterButton,
      activeActionFilterButton,
      metricFilterButton,
      makeNegButton(filterMetricFactory)
    ),
    Some(nodeTypeMenu),
    true,
    engine
  )
  addChildren(filterMenu) // todo add not

  /* ----- Action Filter Menu ----- */
  def makeFilterTargetButton(
      filterType: (FilterType, String),
      targetMenu: Menu
  ): ButtonObject =
    ButtonObject(
      filterType._2,
      () => {
        this.filterType = filterType._1
        targetMenu.open()
      },
      engine
    )
  import FilterType._
  /* __ Active __ */
  val activeActionFilterTargetMenu = makeTargetMenu(filterTargetFactory)
  val activeActionFilterButtons: List[ButtonObject] =
    ButtonObject("Idling", () => this.filterType = Idling, engine)
      :: List(
        (Attacking, "Attacking"),
        (MovingTo, "Moving to"),
        (FleeingFrom, "Fleeing\nfrom"),
        (CanAttack, "Can\nattack")
      ).map(makeFilterTargetButton(_, activeActionFilterTargetMenu))
  val activeActionFilterMenu: MultipleChoiceMenu = MultipleChoiceMenu(
    activeActionFilterButtons,
    Some(filterMenu),
    true,
    engine
  )
  addChildren(activeActionFilterMenu)
  activeActionFilterTargetMenu.uiParent = Some(activeActionFilterMenu)
  /* __ Passive __ */
  val passiveActionFilterTargetMenu = makeTargetMenu(filterTargetFactory)
  val passiveActionFilterButtons: List[ButtonObject] =
    List(
      (ApproachedBy, "Approached\nby"),
      (AttackedBy, "Attacked\nby"),
      (FledBy, "Fled\nby"),
      (CanBeAttackedBy, "Can be\nattacked\nby")
    ).map(makeFilterTargetButton(_, passiveActionFilterTargetMenu))
  val passiveActionFilterMenu: MultipleChoiceMenu = MultipleChoiceMenu(
    passiveActionFilterButtons,
    Some(filterMenu),
    true,
    engine
  )
  addChildren(passiveActionFilterMenu)
  passiveActionFilterTargetMenu.uiParent = Some(passiveActionFilterMenu)

  /* ----- Metric Filter Menu ----- */
  val filterComparaisonMenu =
    makeComparaisonMenu(filterMetricFactory, List(1, 10, 100, -1, -10, -100))
  val filterMetricMenu = makeMetricMenu(filterMetricFactory)
  val filterComparaisonMenuButton = ButtonObject(
    "Comparaison",
    () => {
      filterComparaisonMenu.open()
    },
    engine
  )
  val filterMetricButton = ButtonObject(
    "Metric",
    () => {
      filterMetricMenu.open()
    },
    engine
  )
  val metricFilterMenu: MultipleChoiceMenu = MultipleChoiceMenu(
    List(filterMetricButton, filterComparaisonMenuButton),
    Some(filterMenu),
    false,
    engine
  )
  addChildren(metricFilterMenu)
  filterComparaisonMenu.uiParent = Some(metricFilterMenu)
  filterMetricMenu.uiParent = Some(metricFilterMenu)

  /* ----- Comparaison Menu ----- */
  def makeComparaisonMenu(
      value: Value,
      increments: List[Float]
  ): MultipleChoiceMenu = {
    val valueMenu = makeValueMenu(conditionValue, increments)
    val operatorMenu = makeOperatorMenu(value)
    val valueButton = ButtonObject(
      "Value",
      () => {
        valueMenu.open()
      },
      engine
    )
    val operatorButton = ButtonObject(
      "Operator",
      () => {
        operatorMenu.open()
      },
      engine
    )
    val compMenu: MultipleChoiceMenu = MultipleChoiceMenu(
      List(valueButton, operatorButton),
      Some(conditionMenu),
      false,
      engine
    )
    valueMenu.uiParent = Some(compMenu)
    operatorMenu.uiParent = Some(compMenu)
    addChildren(compMenu)
    compMenu
  }

  /* ----- Value Menu ----- */
  def makeValueMenu(value: Value, increments: List[Float]) =
    lazy val currentValueButton: ButtonObject = ButtonObject(
      "0",
      () => {
        value.value = 0
        currentValueButton.changeText("0")
      },
      engine
    )
    def addValueButton(toAdd: Float): ButtonObject =
      ButtonObject(
        if toAdd > 0 then "+" + toAdd.toString else toAdd.toString,
        () => {
          value.value += toAdd
          currentValueButton.changeText(value.value.toString)
        },
        engine
      )
    val addValueButtons: List[ButtonObject] = increments.map(addValueButton)
    val conditionValueMenu = MultipleChoiceMenu(
      currentValueButton :: addValueButtons,
      None,
      false,
      engine
    )
    addChildren(conditionValueMenu)
    conditionValueMenu

  /* ----- Conditon operator Menu ----- */
  def makeOperatorMenu(value: Value): MultipleChoiceMenu =
    val operatorMenu = MultipleChoiceMenu(
      List(
        ButtonObject(
          "==",
          () => {
            value.compType = CompType.Equals
          },
          engine
        ),
        ButtonObject(
          "<",
          () => {
            value.compType = CompType.LessThan
          },
          engine
        ),
        ButtonObject(
          ">",
          () => {
            value.compType = CompType.GreaterThan
          },
          engine
        )
      ),
      None,
      true,
      engine
    )
    addChildren(operatorMenu)
    operatorMenu

  /* ----- Target Menu ----- */
  def makeTargetMenu(factory: TargetFactory): MultipleChoiceMenu =
    val teamMenu = makeTeamMenu(factory)
    def onClickedWarrior = () => {
      factory.targetType = TargetType.Warrior
      teamMenu.open()
    }
    def onClickedBase = () => {
      factory.targetType = TargetType.Base
      teamMenu.open()
    }
    def onClickedSelf = () => {
      factory.targetType = TargetType.Self
    }
    val warriorButton = ButtonObject("Warrior", onClickedWarrior, engine)
    val baseButton = ButtonObject("Base", onClickedBase, engine)
    val selfButton = ButtonObject("Self", onClickedSelf, engine)
    val targetMenu: MultipleChoiceMenu =
      MultipleChoiceMenu(
        List(warriorButton, baseButton, selfButton),
        None, // needs to be set by parent
        true,
        engine
      )
    teamMenu.uiParent = Some(targetMenu)
    addChildren(targetMenu)
    targetMenu

  /* ----- Team Menu ----- */
  def makeTeamMenu(factory: TargetFactory): MultipleChoiceMenu =
    def onClickedAlly = () => {
      factory.team = Team.Ally
    }
    def onClickedEnemy = () => {
      factory.team = Team.Enemy
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
  val selectorMetricMenu = makeMetricMenu(selectorMetricFactory)
  def onClickedLowest(): Unit = {
    selectorType = SelectorType.Lowest
    selectorMetricMenu.open()
  }
  def onClickedHighest(): Unit = {
    selectorType = SelectorType.Highest
    selectorMetricMenu.open()
  }
  val lowestButton = ButtonObject("Lowest", onClickedLowest, engine)
  val highestButton = ButtonObject("Highest", onClickedHighest, engine)
  val selectorMenu =
    MultipleChoiceMenu(
      List(lowestButton, highestButton),
      Some(actionMenu),
      true,
      engine
    )
  selectorMetricMenu.uiParent = Some(selectorMenu)
  addChildren(selectorMenu)

  /* ----- Metric Menu ----- */
  def makeMetricMenu(factory: MetricFactory): MultipleChoiceMenu =
    val targetMenu = makeTargetMenu(factory)
    def onClickedDistance(): Unit = {
      factory.metricType = MetricType.DistanceFromClosest
      targetMenu.open()
    }
    def onClickedHealth(): Unit = {
      factory.metricType = MetricType.Health
    }
    def onClickedHealthPercentage(): Unit = {
      factory.metricType = MetricType.HealthPercentage
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

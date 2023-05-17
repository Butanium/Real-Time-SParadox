package rtsp.battle
import rtsp.objects.RTSPWarrior
import rtsp.battle.WarriorAction
import scala.util.control.NonLocalReturns
import scala.compiletime.ops.boolean
import sfml.system.Vector2

/* ----- Filter ----- */
enum Filter(var position: Vector2[Float]) {
  case LessThan(value: Float, metric: Metric, _position: Vector2[Float])
      extends Filter(_position)
  case GreaterThan(value: Float, metric: Metric, _position: Vector2[Float])
      extends Filter(_position)
  case Equals(value: Float, metric: Metric, _position: Vector2[Float])
      extends Filter(_position)
  case Not(filter: Filter, _position: Vector2[Float]) extends Filter(_position)
  case Attacking(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case AttackedBy(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case MovingTo(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case ApproachedBy(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case FleeingFrom(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case FledBy(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case CanAttack(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case CanBeAttackedBy(val target: Target, _position: Vector2[Float])
      extends Filter(_position)
  case Idling(_position: Vector2[Float]) extends Filter(_position)
  override def toString: String =
    this match
      case LessThan(value, metric, _) =>
        s"${metric} < $value"
      case GreaterThan(value, metric, _) =>
        s"${metric} > $value"
      case Equals(value, metric, _) =>
        s"${metric} = $value"
      case Not(filter, _) =>
        s"Not [${filter}]"
      case Attacking(target, _) =>
        s"Attacking ${target}"
      case AttackedBy(target, _) =>
        s"Attacked by ${target}"
      case MovingTo(target, _) =>
        s"Moving to ${target}"
      case ApproachedBy(target, _) =>
        s"Approached by ${target}"
      case FleeingFrom(target, _) =>
        s"Fleeing from ${target}"
      case FledBy(target, _) =>
        s"Fleed by ${target}"
      case CanAttack(target, _) =>
        s"Can attack ${target}"
      case CanBeAttackedBy(target, _) =>
        s"Can be attacked by ${target}"
      case Idling(_) => "Idling"
}

/* ----- Action Node ----- */
enum Action(
    val target: Target,
    var filters: List[Filter],
    val selector: Selector
) {
  case Attack(_target: Target, _filters: List[Filter], _selector: Selector)
      extends Action(_target, _filters, _selector)
  case Move(_target: Target, _filters: List[Filter], _selector: Selector)
      extends Action(_target, _filters, _selector)
  case Flee(_target: Target, _filters: List[Filter], _selector: Selector)
      extends Action(_target, _filters, _selector)
  case Idle(_filters: List[Filter])
      extends Action(Target.Self, _filters, Selector.Highest(Metric.Health))

  override def toString: String =
    this match
      case Attack(target, _, selector) =>
        s"Attack [${selector}]\n${target}"
      case Move(target, _, selector) =>
        s"Move to [${selector}]\n${target}"
      case Flee(target, _, selector) =>
        s"Flee from [${selector}]\n${target}"
      case Idle(_) => "Idle"
}

enum Target {
  case Warrior(team: Team)
  case Base(team: Team)
  case Self

  override def toString: String =
    this match
      case Target.Warrior(team) => s"Warrior (${team})"
      case Target.Base(team)    => s"Base (${team})"
      case Target.Self          => "Self"
}

enum Team {
  case Enemy
  case Ally
}

enum Selector(val metric: Metric) {
  case Lowest(_metric: Metric) extends Selector(_metric)
  case Highest(_metric: Metric) extends Selector(_metric)
  override def toString: String = this match
    case Selector.Lowest(metric)  => s"Lowest ${metric}"
    case Selector.Highest(metric) => s"Highest ${metric}"
}

enum Metric {
  case DistanceFromClosest(val target: Target)
  case Health
  case HealthPercentage
  override def toString: String =
    this match
      case Metric.DistanceFromClosest(target) =>
        s"Distance from ${target}"
      case Metric.Health           => "Health"
      case Metric.HealthPercentage => "Health percentage"

}

/* ----- Condition Node ----- */
enum Condition(var filters: List[Filter]) {
  case Not(condition: Condition) extends Condition(condition.filters)
  case Count(
      target: Target,
      _filters: List[Filter],
      countCondition: CountCondition
  ) extends Condition(_filters)
  override def toString: String = this match
    case Condition.Not(condition) => s"Not [${condition}]"
    case Condition.Count(target, _, countCondition) =>
      countCondition match
        case CountCondition.Equals(value) =>
          s"[${target}] = $value"
        case CountCondition.LessThan(value) =>
          s"[${target}] < $value"
        case CountCondition.GreaterThan(value) =>
          s"[${target}] > $value"
}

enum CountCondition(val value: Int) {
  case Equals(_value: Int) extends CountCondition(_value)
  case LessThan(_value: Int) extends CountCondition(_value)
  case GreaterThan(_value: Int) extends CountCondition(_value)
}

enum BehaviorTree(var position: Vector2[Float]) {
  case ActionNode(action: Action, _position: Vector2[Float])
      extends BehaviorTree(_position)
  case Node(children: List[BehaviorTree], _position: Vector2[Float])
      extends BehaviorTree(_position)
  case ConditionNode(
      condition: Condition,
      children: List[BehaviorTree],
      _position: Vector2[Float]
  ) extends BehaviorTree(_position)
  override def toString: String =
    this match
      case ActionNode(action, _)          => action.toString
      case Node(_, _)                     => "Node"
      case ConditionNode(condition, _, _) => condition.toString
}

class Behavior(var tree: BehaviorTree, val battle: RTSPBattle) {

  /** Evaluate the behavior tree and execute the action if it is valid.
    * @param warrior
    *   The warrior that will execute the action
    */
  def evaluate(warrior: RTSPWarrior) = {
    if (!evaluateNode(warrior, tree)) {
      warrior.nextAction = WarriorAction.Idle
    }
  }

  /** Evaluate a node of the behavior tree.
    * @param warrior
    *   The warrior that will execute the action
    * @param tree
    *   The node to evaluate
    * @return
    *   true if an action was executed, false otherwise
    */
  def evaluateNode(warrior: RTSPWarrior, tree: BehaviorTree): Boolean = {
    import BehaviorTree._
    tree match
      case ActionNode(action, _) => return evaluateAction(warrior, action)
      case Node(children, _) =>
        children.exists(evaluateNode(warrior, _))
      case ConditionNode(condition, children, _) =>
        evaluateCondition(warrior, condition) && children.exists(
          evaluateNode(warrior, _)
        )
  }

  /** Evaluate if an action is valid. If it is, the action is executed.
    * @param warrior
    *   The warrior that will execute the action
    * @param action
    *   The action to evaluate
    * @return
    *   true if the action was executed, false otherwise
    */
  def evaluateAction(warrior: RTSPWarrior, action: Action): Boolean = {
    val targets = evaluateTarget(warrior, action.target)
    var filteredTargets =
      targets.filter(applyFilters(warrior, _, action.filters))
    action match
      case Action.Attack(_, _, _) => {
        filteredTargets = filteredTargets.filter(warrior.canAttack(_))
      }
      case _ => ()
    select(warrior, filteredTargets, action.selector) match {
      case None => false
      case Some(target) =>
        action match
          case Action.Attack(_, _, _) =>
            warrior.nextAction = WarriorAction.Attack(target)
          case Action.Move(_, _, _) =>
            warrior.nextAction = WarriorAction.Move(target)
          case Action.Flee(_, _, _) =>
            warrior.nextAction = WarriorAction.Flee(target)
          case Action.Idle(_) => warrior.nextAction = WarriorAction.Idle
        true
    }
  }

  /** Evaluate if a condition is valid.
    * @param warrior
    *   The warrior that will execute the action
    * @param condition
    *   The condition to evaluate
    * @return
    *   true if the condition is valid, false otherwise
    */
  def evaluateCondition(
      warrior: RTSPWarrior,
      condition: Condition
  ): Boolean = {
    condition match
      case Condition.Not(condition) => !evaluateCondition(warrior, condition)
      case Condition.Count(target, filters, countCondition) =>
        val targets = evaluateTarget(warrior, target)
        val filteredTargets =
          targets.filter(applyFilters(warrior, _, filters))
        countCondition match
          case CountCondition.Equals(value) =>
            filteredTargets.length == value
          case CountCondition.LessThan(value) =>
            filteredTargets.length < value
          case CountCondition.GreaterThan(value) =>
            filteredTargets.length > value
  }

  /** Select a target according to a certain selector
    * @param warrior
    *   The warrior that will execute the action
    * @param targets
    *   The possible targets
    * @param selector
    *   The selector to evaluate
    */
  def select(
      warrior: RTSPWarrior,
      targets: List[RTSPWarrior],
      selector: Selector
  ): Option[RTSPWarrior] = {
    selector match
      case Selector.Lowest(metric) =>
        if (targets.nonEmpty)
          Some(targets.minBy(evaluateMetric(warrior, _, metric)))
        else None
      case Selector.Highest(metric) =>
        if (targets.nonEmpty)
          Some(targets.maxBy(evaluateMetric(warrior, _, metric)))
        else None
  }

  /** Evaluate a metric
    * @param warrior
    *   The warrior that we currently evaluate
    * @param target
    *   The target that we evaluate
    * @param metric
    *   The metric to evaluate
    */
  def evaluateMetric(
      warrior: RTSPWarrior,
      target: RTSPWarrior,
      metric: Metric
  ): Float = {
    metric match
      case Metric.DistanceFromClosest(targetType) =>
        warrior.distanceTo(target).toFloat
      case Metric.Health => target.health.toFloat
      case Metric.HealthPercentage =>
        target.health.toFloat / target.maxHealth.toFloat * 100
  }

  /** Evaluate the target type and returns the possible targets
    * @param warrior
    *   The warrior that will execute the action
    * @param targetType
    *   The target type to evaluate
    */
  def evaluateTarget(
      warrior: RTSPWarrior,
      target: Target
  ): List[RTSPWarrior] = {
    target match
      case Target.Self       => List(warrior)
      case Target.Base(team) => List(battle.bases(evaluateTeam(warrior, team)))
      case Target.Warrior(team) =>
        battle.getWarriors(evaluateTeam(warrior, team))
  }

  /** Convert a team to an integer
    * @param warrior
    *   The warrior that will execute the action
    * @param team
    *   The team to convert
    */
  def evaluateTeam(warrior: RTSPWarrior, team: Team): Int = {
    team match
      case Team.Enemy => 1 - warrior.team
      case Team.Ally  => warrior.team
  }

  /** Evaluate the target filters and return the target
    * @param warrior
    *   The warrior we are evaluating
    * @param filters
    *   The filters to evaluate
    * @return
    *   true if the target is fulfills all the filters, false otherwise
    */
  def applyFilters(
      warrior: RTSPWarrior,
      target: RTSPWarrior,
      filters: List[Filter]
  ): Boolean = {
    filters.forall(applyFilter(warrior, target, _))
  }

  /** Check whether a target fulfills a filter
    * @param warrior
    *   The warrior we are evaluating
    * @param toFilter
    *   The target we are evaluating
    * @param filter
    *   The filter to evaluate
    * @return
    *   true if the target fulfills the filter, false otherwise
    */
  def applyFilter(
      warrior: RTSPWarrior,
      toFilter: RTSPWarrior,
      filter: Filter
  ): Boolean = {
    import Filter._
    filter match
      case Not(filter, _) => !applyFilter(warrior, toFilter, filter)
      case GreaterThan(value, metric, _) =>
        evaluateMetric(warrior, toFilter, metric) > value
      case LessThan(value, metric, _) =>
        evaluateMetric(warrior, toFilter, metric) < value
      case Equals(value, metric, _) =>
        evaluateMetric(warrior, toFilter, metric) == value
      case Attacking(target, _) =>
        evaluateTarget(warrior, target).exists(toFilter.isAttacking(_))
      case AttackedBy(target, _) =>
        evaluateTarget(warrior, target).exists(_.isAttacking(toFilter))
      case MovingTo(target, _) =>
        evaluateTarget(warrior, target).exists(toFilter.isMovingTo(_))
      case ApproachedBy(target, _) =>
        evaluateTarget(warrior, target).exists(_.isMovingTo(toFilter))
      case CanAttack(target, _) =>
        evaluateTarget(warrior, target).exists(toFilter.canAttack(_))
      case CanBeAttackedBy(target, _) =>
        evaluateTarget(warrior, target).exists(_.canAttack(toFilter))
      case FledBy(target, _) =>
        evaluateTarget(warrior, target).exists(toFilter.isFleeing(_))
      case FleeingFrom(target, _) =>
        evaluateTarget(warrior, target).exists(_.isFleeing(toFilter))
      case Idling(_) => toFilter.isIdle
  }

}

object Behavior {
  import Action._
  import Filter._
  import Target._
  import BehaviorTree._
  import Selector._
  import Metric._
  import Team._
  import Condition._
  def basicBehavior(battle: RTSPBattle) =
    Behavior(
      Node(
        List(
          ActionNode(
            Attack(
              Warrior(Enemy),
              List(),
              Lowest(DistanceFromClosest(Self))
            ),
            (10, 300)
          ),
          ActionNode(
            Move(Warrior(Enemy), List(), Lowest(DistanceFromClosest(Self))),
            (100, 300)
          ),
          ActionNode(
            Attack(Base(Enemy), List(), Lowest(DistanceFromClosest(Self))),
            (200, 300)
          ),
          ActionNode(
            Move(Base(Enemy), List(), Lowest(DistanceFromClosest(Self))),
            (300, 300)
          )
        ),
        (150, 50)
      ),
      battle
    )

  def advancedBehavior(battle: RTSPBattle) =
    Behavior(
      Node(
        List(
          ConditionNode(
            Count(
              Warrior(Enemy),
              List(Attacking(Self, (400, 200)), CanAttack(Self, (600, 200))),
              CountCondition.GreaterThan(0)
            ),
            List(
              ConditionNode(
                Count(
                  Self,
                  List(LessThan(50f, HealthPercentage, (400, 500))),
                  CountCondition.GreaterThan(0)
                ),
                List(
                  ActionNode(
                    Flee(
                      Warrior(Enemy),
                      List(Attacking(Self, (600, 600))),
                      Lowest(DistanceFromClosest(Self))
                    ),
                    (500, 500)
                  )
                ),
                (500, 400)
              )
            ),
            (500, 20)
          ),
          basicBehavior(battle).tree
        ),
        (150, 0)
      ),
      battle
    )
}

package rtsp.battle
import rtsp.objects.RTSPWarrior
import rtsp.battle.WarriorAction
import scala.util.control.NonLocalReturns
import scala.compiletime.ops.boolean
import sfml.system.Vector2

/* ----- Filter ----- */
enum Filter {
  case All
  case LessThan(value: Float, metric: Metric)
  case GreaterThan(value: Float, metric: Metric)
  case Equals(value: Float, metric: Metric)
  case Not(filter: Filter)
  case Attacking(val target: Target)
  case AttackedBy(val target: Target)
  case MovingTo(val target: Target)
  case ApproachedBy(val target: Target)
  case FleeingFrom(val target: Target)
  case FleedBy(val target: Target)
  case CanAttack(val target: Target)
  case CanBeAttackedBy(val target: Target)
  case Idling
}

/* ----- Action Node ----- */
enum Action(
    val target: Target,
    val filters: List[Filter],
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
}

enum Target(var team: Team) {
  case Warrior(_team: Team) extends Target(_team)
  case Base(_team: Team) extends Target(_team)
  case Self extends Target(Team.Ally)
}

enum Team {
  case Enemy
  case Ally
}

enum Selector(val metric: Metric) {
  case Lowest(_metric: Metric) extends Selector(_metric)
  case Highest(_metric: Metric) extends Selector(_metric)
}

enum Metric {
  case DistanceFromClosest(val target: Target)
  case Health
  case HealthPercentage
}

/* ----- Condition Node ----- */
enum Condition {
  case Not(condition: Condition)
  case Count(
      target: Target,
      filter: List[Filter],
      countCondition: CountCondition
  )
}

enum CountCondition {
  case Equals(value: Int)
  case LessThan(value: Int)
  case GreaterThan(value: Int)
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
        target.health.toFloat / target.maxHealth.toFloat
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
    val team = evaluateTeam(warrior, target.team)
    target match
      case Target.Self       => List(warrior)
      case Target.Base(_)    => List(battle.bases(team))
      case Target.Warrior(_) => battle.getWarriors(team)
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
      case All         => true
      case Not(filter) => !applyFilter(warrior, toFilter, filter)
      case GreaterThan(value, metric) =>
        evaluateMetric(warrior, toFilter, metric) > value
      case LessThan(value, metric) =>
        evaluateMetric(warrior, toFilter, metric) < value
      case Equals(value, metric) =>
        evaluateMetric(warrior, toFilter, metric) == value
      case Attacking(target) =>
        evaluateTarget(warrior, target).exists(toFilter.isAttacking(_))
      case AttackedBy(target) =>
        evaluateTarget(warrior, target).exists(_.isAttacking(toFilter))
      case MovingTo(target) =>
        evaluateTarget(warrior, target).exists(toFilter.isMovingTo(_))
      case ApproachedBy(target) =>
        evaluateTarget(warrior, target).exists(_.isMovingTo(toFilter))
      case CanAttack(target) =>
        evaluateTarget(warrior, target).exists(toFilter.canAttack(_))
      case CanBeAttackedBy(target) =>
        evaluateTarget(warrior, target).exists(_.canAttack(toFilter))
      case FleedBy(target) =>
        evaluateTarget(warrior, target).exists(toFilter.isFleeing(_))
      case FleeingFrom(target) =>
        evaluateTarget(warrior, target).exists(_.isFleeing(toFilter))
      case Idling => toFilter.isIdle
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
              List(All),
              Lowest(DistanceFromClosest(Self))
            ),
            (10, 300)
          ),
          ActionNode(
            Move(Warrior(Enemy), List(All), Lowest(DistanceFromClosest(Self))),
            (100, 300)
          ),
          ActionNode(
            Attack(Base(Enemy), List(All), Lowest(DistanceFromClosest(Self))),
            (200, 300)
          ),
          ActionNode(
            Move(Base(Enemy), List(All), Lowest(DistanceFromClosest(Self))),
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
              List(Attacking(Self), CanAttack(Self)),
              CountCondition.GreaterThan(0)
            ),
            List(
              ConditionNode(
                Count(
                  Self,
                  List(LessThan(0.5f, HealthPercentage)),
                  CountCondition.GreaterThan(0)
                ),
                List(
                  ActionNode(
                    Flee(
                      Warrior(Enemy),
                      List(Attacking(Self)),
                      Lowest(DistanceFromClosest(Self))
                    ),
                    (300, 500)
                  )
                ),
                (300, 400)
              )
            ),
            (300, 20)
          ),
          basicBehavior(battle).tree
        ),
        (150, 0)
      ),
      battle
    )
}

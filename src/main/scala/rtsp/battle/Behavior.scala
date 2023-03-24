package rtsp.battle
import rtsp.objects.RTSPWarrior
import rtsp.battle.WarriorAction
import scala.util.control.NonLocalReturns

enum Action(val targetType: TargetType) {
  case Attack(_targetType: TargetType) extends Action(_targetType)
  case Move(_targetType: TargetType) extends Action(_targetType)
}

enum TargetType(val targetFilter: TargetFilter) {
  case Enemy(_targetFilter: TargetFilter) extends TargetType(_targetFilter)
  case Ally(_targetFilter: TargetFilter) extends TargetType(_targetFilter)
  case AllyBase(_targetFilter: TargetFilter) extends TargetType(_targetFilter)
  case EnemyBase(_targetFilter: TargetFilter) extends TargetType(_targetFilter)
}

enum TargetFilter {
  case Closest
}
// enum Condition {
//   case Health()
// }

enum BehaviorTree {
  case ActionNode(action: Action)
  case Node(children: List[BehaviorTree])
  // case ConditionNode()
}

class Behavior(val tree: BehaviorTree, val battle: RTSPBattle) {

  def evaluate(warrior: RTSPWarrior) = {
    evaluateNode(warrior, tree)
  }
  def evaluateNode(warrior: RTSPWarrior, tree: BehaviorTree): Boolean = {
    import BehaviorTree._
    val actionFound = {
      tree match
        case ActionNode(action) => return evaluateAction(warrior, action)
        case Node(children) =>
          children.exists(evaluateNode(warrior, _))
    }
    if (!actionFound) {
      warrior.action = WarriorAction.Idle
    }
    actionFound
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
    action match
      case Action.Attack(targetType) => {
        val potentialTargets =
          evaluateTargetType(warrior, targetType).filter(warrior.canAttack(_))
        evaluateTargetFilter(
          warrior,
          targetType.targetFilter,
          potentialTargets
        ) match {
          case None => false
          case Some(target) =>
            warrior.action = WarriorAction.Attack(target)
            true
        }
      }
      case Action.Move(targetType) =>
        evaluateTargetFilter(
          warrior,
          targetType.targetFilter,
          evaluateTargetType(warrior, targetType)
        ) match {
          case None => false
          case Some(target) =>
            warrior.rooted = false
            warrior.action = WarriorAction.Move(target)
            true
        }

  }

  /** Evaluate the target type and returns the possible targets
    * @param warrior
    *   The warrior that will execute the action
    * @param targetType
    *   The target type to evaluate
    */
  def evaluateTargetType(
      warrior: RTSPWarrior,
      targetType: TargetType
  ): List[RTSPWarrior] = {
    targetType match
      case TargetType.Enemy(filter)     => battle.getEnemies(warrior.team)
      case TargetType.Ally(filter)      => battle.getAllies(warrior.team)
      case TargetType.EnemyBase(filter) => List(battle.bases(1 - warrior.team))
      case TargetType.AllyBase(filter)  => List(battle.bases(warrior.team))

  }

  /** Evaluate the target filter and return the target
    * @param warrior
    *   The warrior that will execute the action
    * @param targetFilter
    *   The target filter to evaluate
    */
  def evaluateTargetFilter(
      warrior: RTSPWarrior,
      targetFilter: TargetFilter,
      potentialTargets: List[RTSPWarrior]
  ): Option[RTSPWarrior] = {
    targetFilter match
      case TargetFilter.Closest =>
        if (potentialTargets.nonEmpty)
          Some(potentialTargets.minBy(warrior.distanceTo(_)))
        else None
  }
}

object Behavior {
  import Action._
  import TargetFilter._
  import TargetType._
  import BehaviorTree._
  def basicBehavior(battle: RTSPBattle) =
    Behavior(
      Node(
        List(
          ActionNode(Attack(Enemy(Closest))),
          ActionNode(Move(Enemy(Closest)))
        )
      ),
      battle
    )
}

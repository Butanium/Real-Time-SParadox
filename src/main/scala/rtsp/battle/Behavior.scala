package rtsp.battle
import rtsp.objects.RTSPWarrior

enum TargetType {
  case Enemy
  case Ally
}

enum TargetFilter {
  case Closest(targetType : TargetType)
}

enum Action(val targetFilter : TargetFilter) {
  case Attack(_targetFilter: TargetFilter) extends Action(_targetFilter)
  case Move(_targetFilter: TargetFilter) extends Action(_targetFilter)
}

// enum Condition {
//   case Health()
// }

enum BehaviorTree {
  case ActionNode(action: Action)
  case Node(children : List[BehaviorTree])
  // case ConditionNode()
}
class Behavior(tree : BehaviorTree) {
  def evaluate(warrior : RTSPWarrior) : Unit = {

  }

}


object Behavior {
  import Action._
  import TargetFilter._
  import TargetType._
  import BehaviorTree._
  def basicBehavior = 
    Node(List(ActionNode(Attack(Closest(Enemy)))))
}
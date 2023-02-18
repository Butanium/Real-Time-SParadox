package rtsp.objects
import engine2D.objects.GameUnit
import rtsp.RTSPGameEngine
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior

/*
  Utiliser l'arbre de comportement
  Chaque warrior a une équipe: 0 = joueur, 1 = ennemi
 */

enum WarriorAction(target: RTSPWarrior) {
  case Attack(_target: RTSPWarrior) extends WarriorAction(_target)
  case Move(_target: RTSPWarrior) extends WarriorAction(_target)
  case Idle extends WarriorAction(null)
}

class RTSPWarrior(
    engine: RTSPGameEngine,
    battle: RTSPBattle,
    var team: Int,
    var range: Int,
    var attackDamage: Int,
    var behavior: Behavior,
    var attackDelay: Float
) extends GameUnit(100, 1f, engine, baseRotation = 0, active = true) {
  import WarriorAction.*
  var action = Idle
  var currentAttackDelay = attackDelay
  def attack(target: RTSPWarrior): Unit =
    if (currentAttackDelay < 0) then { target.health -= attackDamage }
    else { currentAttackDelay -= engine.deltaTime }

  def executeMove(target: engine2D.objects.GameTransform): Unit = {

  }

  def executeAction(): Unit = {
    action match
      case Attack(target) => attack(target)
      case Move(target)   => executeMove(target)
      case Idle           => ()
  }
  def canAttack(target: RTSPWarrior): Boolean = {
    distanceTo(target) <= range
  }
  def canMove(target: engine2D.objects.GameTransform) = true

  override def update(): Unit = {
    super.update()
    attack()
  }

}

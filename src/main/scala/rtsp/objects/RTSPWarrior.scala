package rtsp.objects
import engine2D.objects.GameUnit
import rtsp.RTSPGameEngine
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior

/*
  Utiliser l'arbre de comportement
  Chaque warrior a une équipe: 0 = joueur, 1 = ennemi
 */

class RTSPWarrior(
    engine: RTSPGameEngine,
    battle: RTSPBattle,
    var team: Int,
    var range: Int,
    var attackDamage: Int,
    var behavior: Behavior,
    var attackDelay: Float
) extends GameUnit(100, 1f, engine, baseRotation = 0, active = true) {
  var target: Option[RTSPWarrior] = None
  var currentAttackDelay = attackDelay
  def attack(): Unit =
    target match
      case None => currentAttackDelay = attackDelay
      case Some(warrior: RTSPWarrior) =>
        if (currentAttackDelay < 0) then { warrior.health -= attackDamage }
        else { currentAttackDelay -= engine.deltaTime }
  def canAttack(target: RTSPWarrior): Boolean = {
    distanceTo(target) <= range
  }
  def canMove(target: engine2D.objects.GameTransform) = true

  override def update(): Unit = {
    super.update()
    attack()
  }

}

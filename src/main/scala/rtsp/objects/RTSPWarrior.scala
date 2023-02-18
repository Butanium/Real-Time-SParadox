package rtsp.objects
import engine2D.objects.GameUnit
import rtsp.RTSPGameEngine
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior
import rtsp.battle.WarriorAction
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager
import engine2D.GameEngine
/*
  Utiliser l'arbre de comportement
  Chaque warrior a une équipe: 0 = joueur, 1 = ennemi
 */

class RTSPWarrior(
    engine: GameEngine,
    battle: RTSPBattle,
    var team: Int,
    var maxHP: Int,
    var range: Int,
    var attackDamage: Int,
    var behavior: Behavior,
    var attackDelay: Float,
    val sprite: SpriteObject,
    val debug: Boolean = false
) extends GameUnit(maxHP, 1f, engine, baseRotation = 0) {
  import WarriorAction.*
  var action = Idle
  var currentAttackDelay = attackDelay
  add(sprite)
  def attack(target: RTSPWarrior): Unit = {
    if debug then println(f"can attack target: ${canAttack(target)}, distance: ${distanceTo(target)}")
    rooted = true
    if (currentAttackDelay < 0) then { target.health -= attackDamage }
    else { currentAttackDelay -= engine.deltaTime }
  }

  def executeMove(target: engine2D.objects.GameTransform): Unit = {
    rooted = false
    changeDirectionTo(target)
  }

  def executeAction(): Unit = {
    action match
      case Attack(target) =>
        sprite.color = sfml.graphics.Color.Blue(); attack(target);
        target.sprite.color = sfml.graphics.Color.Red()
      case Move(target) => executeMove(target); currentAttackDelay = attackDelay
      case Idle         => currentAttackDelay = attackDelay; rooted = true
  }
  def canAttack(target: RTSPWarrior): Boolean = {
    distanceTo(target) <= range
  }
  def canMove(target: engine2D.objects.GameTransform) = true

  override def update(): Unit = {
    super.update()
    executeAction()
    if (debug) {
      println(s"Warrior $this")
      println(s"  team: $team")
      println(s"  action: $action")
      println(s"  behavior: $behavior")
    }
  }
}

object RTSPWarrior {
  def createArcher(
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior,
      debug: Boolean = false
  ) =
    new RTSPWarrior(
      engine,
      battle,
      team,
      1000,
      5,
      10,
      behavior,
      1f,
      engine2D.objects.SpriteObject(
        TextureManager.getTexture("warriors/archer.png"),
        engine
      ),
      debug = debug
    )
  def createBarbarian(
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior,
      debug: Boolean = false
  ) =
    new RTSPWarrior(
      engine,
      battle,
      team,
      1800,
      1,
      20,
      behavior,
      0.5f,
      engine2D.objects.SpriteObject(
        TextureManager.getTexture("warriors/warrior.png"),
        engine
      ),
      debug = debug
    )
}

package rtsp.objects
import engine2D.objects.GameUnit
import rtsp.RTSPGameEngine
import rtsp.battle.RTSPBattle
import rtsp.battle.Behavior
import rtsp.battle.WarriorAction
import engine2D.objects.SpriteObject
import engine2D.graphics.TextureManager
import engine2D.GameEngine
import engine2D.objects.Grabbable
import sfml.window.Mouse

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
    var attackDelay: Float,
    speed: Float,
    var behavior: Behavior,
    val sprite: SpriteObject,
    val debug: Boolean = false
) extends GameUnit(maxHP, speed, engine)
with Grabbable(Mouse.Button.Left, engine, debug = debug) {
  setOriginToCenter(sprite.globalBounds)
  import WarriorAction.*
  var action = Idle
  var currentAttackDelay = attackDelay
  add(sprite)
  def attack(target: RTSPWarrior): Unit = {
    if debug then
      println(
        f"${id} can attack target ${target.id}: ${canAttack(target)}, distance: ${distanceTo(target)}"
      )
    rooted = true
    if (currentAttackDelay < 0) then { target.health -= attackDamage }
    else { currentAttackDelay -= engine.deltaTime }
  }

  def executeMove(target: engine2D.objects.GameTransform): Unit = {
    rooted = false
    changeDirectionTo(target)
  }

  override def onDeath(): Unit =
    active = false

  def executeAction(): Unit = {
    assert(active)
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

  override def onUpdate(): Unit = {
    executeAction()
    if (debug) {
      println(s"Warrior ${this.id}")
      println(s"  team: $team")
      println(s"  action: $action")
      println(s"  behavior: $behavior")
    }
    super.onUpdate()
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
      maxHP = 1000,
      range = 100,
      attackDamage = 10,
      attackDelay = 1f,
      speed = 10f,
      behavior,
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
      maxHP = 1800,
      range = 10,
      attackDamage = 20,
      attackDelay = 0.5f,
      speed = 20f,
      behavior,
      engine2D.objects.SpriteObject(
        TextureManager.getTexture("warriors/warrior.png"),
        engine
      ),
      debug = debug
    )
}

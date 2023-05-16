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
import sfml.system.Vector2
import rtsp.Constants
import engine2D.objects.RectangleObject
import sfml.graphics.Color
import engine2D.objects.OnHover

/*
  Utiliser l'arbre de comportement
  Chaque warrior a un id d'équipe
 */

class RTSPWarrior(
    engine: GameEngine,
    battle: RTSPBattle,
    val team: Int,
    var maxHP: Int,
    var range: Int,
    var attackDamage: Int,
    var attackDelay: Float,
    speed: Float,
    var behavior: Behavior,
    val spriteTexture: String,
    val debug: Boolean = false,
    var benched: Boolean = false,
    val price: Int = 1,
    val name: String = "TemplateWarrior"
) extends GameUnit(maxHP, speed, engine)
    with Grabbable(Mouse.Button.Left, engine, debug = debug)
    with Buyable 
    with OnHover
    {
  
  var sprite = SpriteObject(TextureManager.getTexture(spriteTexture), engine)
  val healthBar = new HealthBar(this, engine)
  healthBar.zIndex = 3
  // Compense le fait que l'origine du sprite du warrior est au centre
  healthBar.addOffset((-sprite.globalBounds.width / 2f, -sprite.globalBounds.height / 2f)) 
  engine.spawn(healthBar)
  initShowOnHover(healthBar, this)

  /** The amount of frames the warrior is stunned */
  private var stunTime = 0
  def stunned = stunTime > 0

  /** Stuns the warrior
    * @param duration
    *   The duration of the stun in seconds
    */
  def stun(duration: Float): Unit = stunTime =
    math.max(stunTime / engine.deltaTime, duration.toFloat).ceil.toInt
  this.rotationEnabled = false
  def contains(point: Vector2[Float]) = sprite.globalBounds.contains(point)
  setOriginToCenter(sprite.globalBounds)
  import WarriorAction.*
  // The action the warrior will execute in the next frame
  var nextAction = Idle
  // The action the warrior is currently executing
  var currentAction = Idle

  def isAttacking(target: RTSPWarrior) =
    currentAction match
      case Attack(realTarget) => realTarget == target
      case _                  => false

  def isMovingTo(target: RTSPWarrior) =
    currentAction match
      case Move(realTarget) => realTarget == target
      case _                => false

  def isIdle = currentAction == Idle

  def isFleeing(target: RTSPWarrior) =
    currentAction match
      case Flee(realTarget) => realTarget == target
      case _                => false

  private var grabLocation: Vector2[Float] = Vector2(0, 0)
  setOnGrab(() => { grabLocation = position })

  /** The position before the battle started */
  var initialPosition: Vector2[Float] = Vector2(0, 0)
  private var currentAttackDelay = attackDelay
  add(sprite)
  def attack(target: RTSPWarrior): Unit = {
    if debug then
      println(
        f"${id} can attack target ${target.id}: ${canAttack(target)}, distance: ${distanceTo(target)}"
      )
    rooted = true
    if (currentAttackDelay < 0) then {
      if this.name == "Archer" then {
        val arrow = new Arrow(engine, this, target)
        engine.spawn(arrow)}
      target.takeDamage(attackDamage)
      currentAttackDelay = attackDelay
    } else { currentAttackDelay -= engine.deltaTime }
  }

  def executeMove(targetPosition: Vector2[Float]): Unit = {
    rooted = false
    changeDirectionTo(targetPosition)
  }

  def executeFlee(targetPosition: Vector2[Float]): Unit = {
    rooted = false
    changeDirection(position - targetPosition)
  }

  override def onDeath(): Unit =
    active = false
    healthBar.active = false

  def executeAction(action: WarriorAction): Unit = {
    assert(active)
    sprite.color = sfml.graphics.Color.White()
    action match
      case Attack(target) =>
        sprite.color = sfml.graphics.Color.Red();
        attack(target);
      case Move(target) =>
        executeMove(target.position); currentAttackDelay = attackDelay
      case Flee(target) =>
        executeFlee(target.position); currentAttackDelay = attackDelay
      case Idle => currentAttackDelay = attackDelay; rooted = true
  }
  def canAttack(target: RTSPWarrior): Boolean = {
    distanceTo(target) <= range
  }
  def canMove(target: engine2D.objects.GameTransform) = true

  override def onUpdate(): Unit = {
    if (stunned) then {
      stunTime -= 1
      currentAction = Idle
      executeAction(Idle)
    } else {
      currentAction = nextAction
      if (debug) then
        println(s"Warrior ${this.id}")
        println(s"  team: $team")
        println(s"  action: $currentAction")
        println(s"  behavior: $behavior")
    }
    executeAction(currentAction)

    if (position.x < 0) {
        position = (0f, position.y)
    }
    if (position.x > engine.window.size.x) {
        position = ((engine.window.size.x).toFloat, position.y)
    }
    if (position.y < 0) {
        position = (position.x, 0f)
    }
    if (position.y > engine.window.size.y) {
        position = (position.x, (engine.window.size.y).toFloat)
    }

    super.onUpdate()
  }

  private def resetSprite() = {
    sprite.color = sfml.graphics.Color.White()
  }

  def reset(): Unit = {
    resetSprite()
    active = true
    health_ = maxHP
    position = initialPosition
    nextAction = Idle
    currentAction = Idle
    currentAttackDelay = attackDelay
    rooted = true
    rotation = baseRotation
  }

}

object RTSPWarrior {
  def createArcher(
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior,
      debug: Boolean = false,
      benched: Boolean = false
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
      "warriors/archer.png",
      name = "Archer",
      price = 4,
      debug = debug
    )
  def createBarbarian(
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior,
      debug: Boolean = false,
      benched: Boolean = false
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
      "warriors/warrior.png",
      name = "Barbarian",
      price = 3,
      debug = debug
    )
  def createGiant(
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior,
      debug: Boolean = false,
      benched: Boolean = false
  ) =
    val w = new RTSPWarrior(
      engine,
      battle,
      team,
      maxHP = 3000,
      range = 10,
      attackDamage = 10,
      attackDelay = 1f,
      speed = 7f,
      behavior,
      "warriors/giant.png",
      name = "Giant",
      price = 2,
      debug = debug
    )
    w.scale(1.5f, 1.5f)
    w

  private val warriorTypes: Array[
    (GameEngine, RTSPBattle, Int, Behavior, Boolean, Boolean) => RTSPWarrior
  ] = new Array(2)
  warriorTypes(Constants.ID_ARCHER) = createArcher
  warriorTypes(Constants.ID_BARBARIAN) = createBarbarian
  def apply(
      typeId: Int,
      engine: GameEngine,
      battle: RTSPBattle,
      team: Int,
      behavior: Behavior = null,
      debug: Boolean = false,
      benched: Boolean = false
  ) =
    val _behavior =
      if behavior == null then Behavior.basicBehavior(battle) else behavior
    warriorTypes(typeId)(engine, battle, team, _behavior, debug, benched)

  implicit def ordering: Ordering[RTSPWarrior] =
    Ordering.by(e => e.id)
}

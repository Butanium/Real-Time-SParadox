package rtsp.objects
import rtsp.objects.RTSPWarrior
import engine2D.GameEngine
import rtsp.Player
import engine2D.objects.SpriteObject
import rtsp.battle.RTSPBattle
import engine2D.objects.GameObject
import engine2D.objects.Grabbable
import sfml.window.Mouse
import sfml.system.Vector2
import engine2D.graphics.TextureManager

// Il faut faire en sorte qu'un buff ne soit utilisé que sur des warriors de l'équipe du joueur

abstract class Effect(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    sprite: SpriteObject,
    debug: Boolean
) extends GameObject(engine)
    with Grabbable(Mouse.Button.Left, engine, debug = debug) {
  sprite.boundDimensions(16f, 16f)
  setOriginToCenter(sprite.globalBounds)
  addChildren(sprite)
  var grabLocation: Vector2[Float] = null
  setOnGrab(() => grabLocation = position)
  def contains(point: Vector2[Float]) = sprite.contains(point)
  def apply(warrior: RTSPWarrior): Unit = {
    this.markForDeletion()
  }
}
class AttackBuff(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    sprite: SpriteObject,
    val amount: Int,
    debug: Boolean
) extends Effect(engine, player, battle, sprite, debug) {
  override def apply(warrior: RTSPWarrior): Unit = {
    warrior.attackDamage += amount
    super.apply(warrior)
  }
}
class SpeedBuff(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    sprite: SpriteObject,
    val amount: Int,
    debug: Boolean
) extends Effect(engine, player, battle, sprite, debug) {
  override def apply(warrior: RTSPWarrior): Unit = {
    warrior.speed += amount
    super.apply(warrior)
  }
}
class TankBuff(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    sprite: SpriteObject,
    val amount: Int,
    debug: Boolean
) extends Effect(engine, player, battle, sprite, debug) {
  override def apply(warrior: RTSPWarrior): Unit = {
    warrior.maxHP += amount
    super.apply(warrior)
  }
}

object Effect {
    def createSpeedBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new SpeedBuff(engine, player, battle, engine2D.objects.SpriteObject(
        TextureManager.getTexture("potions/potion_test.png"),
        engine
      ), 10, debug)
    def createAttackBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new AttackBuff(engine, player, battle, engine2D.objects.SpriteObject(
        TextureManager.getTexture("potions/potion_test.png"),
        engine
      ), 10, debug)
    def createTankBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new TankBuff(engine, player, battle, engine2D.objects.SpriteObject(
        TextureManager.getTexture("potions/potion_test.png"),
        engine
      ), 500, debug)
}
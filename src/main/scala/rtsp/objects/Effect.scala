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
import sfml.graphics.Color

// Il faut faire en sorte qu'un buff ne soit utilisé que sur des warriors de l'équipe du joueur

abstract class Effect(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    val spriteTexture: String,
    debug: Boolean
) extends GameObject(engine)
    with Grabbable(Mouse.Button.Left, engine, debug = debug) with Buyable {
  val sprite = SpriteObject(TextureManager.getTexture(spriteTexture), engine)
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
    spriteTexture: String,
    val amount: Int,
    debug: Boolean
) extends Effect(engine, player, battle, spriteTexture, debug) {
  val name = "AttackBuff"
  val price = 5
  override def apply(warrior: RTSPWarrior): Unit = {
    if (warrior.team == player.id) {
      warrior.attackDamage += amount
      super.apply(warrior)
    }
  }
}

class SpeedBuff(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    spriteTexture: String,
    val amount: Float,
    debug: Boolean
) extends Effect(engine, player, battle, spriteTexture, debug) {
  val name = "SpeedBuff"
  val price = 5
  override def apply(warrior: RTSPWarrior): Unit = {
    if (warrior.team == player.id) {
      warrior.speed += amount
      super.apply(warrior)
    }
  }
}

class TankBuff(
    engine: GameEngine,
    player: Player,
    battle: RTSPBattle,
    spriteTexture: String,
    val amount: Int,
    debug: Boolean
) extends Effect(engine, player, battle, spriteTexture, debug) {
  val name = "TankBuff"
  val price = 5
  override def apply(warrior: RTSPWarrior): Unit = {
    if (warrior.team == player.id) {
      warrior.maxHP += amount
      super.apply(warrior)
    }
  }
}

object Effect {
    def createAttackBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new AttackBuff(engine, player, battle, "potions/attack.png", 10, debug)
    def createSpeedBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new SpeedBuff(engine, player, battle, "potions/speed.png", 10, debug)
    def createTankBuff(engine: GameEngine, player: Player, battle: RTSPBattle, debug: Boolean) = new TankBuff(engine, player, battle, "potions/tank.png", 500, debug)
}
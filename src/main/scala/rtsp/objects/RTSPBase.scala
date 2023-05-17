package rtsp.objects

import rtsp.objects.RTSPWarrior
import rtsp.Constants.BattleC.*
import rtsp.Constants.*
import engine2D.graphics.TextureManager
import engine2D.objects.SpriteObject
import rtsp.battle.*
import engine2D.objects.CircleObject
import sfml.system.Vector2

// A base is a special case of warrior, and when it dies, the player loses

class RTSPBase(
    engine: rtsp.RTSPGameEngine,
    battle: RTSPBattle,
    player: rtsp.Player
) extends RangeWarrior[Arrow](
      engine,
      battle,
      player.id,
      BASE_HP,
      BASE_RANGE,
      BASE_ATTACK_DAMAGE,
      BASE_ATTACK_DELAY,
      0f,
      Behavior.basicBehavior(battle),
      "base.png",
      Arrow.factory,
      debug = false,
      benched = false,
      price = 0,
      name = "Base"
    ) {

  override def onDeath(): Unit = {
    battle.addLoser(player.id)
    super.onDeath()
  }
  super.isGrabbable = false

  /** Base is not grabbable */
  override def isGrabbable_=(value: Boolean): Unit = {}

  val spriteScale = Vector2[Float](
    sprite.globalBounds.width,
    sprite.globalBounds.height
  )
  val padding = spriteScale * (1 / 2f) * (if player.id == 0 then -1f else 1f)
  position = Vector2(
    ARENA_BOUNDS.width,
    ARENA_BOUNDS.height
  ) * (1 - player).toFloat + padding
  // Circle that represents the warrior drop radius around the base using SFML
  val circle = CircleObject(WARRIOR_DROP_RADIUS, engine)
  circle.position = player.id match {
    case 0 => position - Vector2(WARRIOR_DROP_RADIUS, WARRIOR_DROP_RADIUS)
    case 1 => Vector2(-WARRIOR_DROP_RADIUS + 50, -WARRIOR_DROP_RADIUS + 50)
  }
  circle.fillColor = sfml.graphics.Color.Transparent()
  circle.outlineColor = sfml.graphics.Color.White()
  circle.outlineThickness = 2
  engine.spawn(circle)
}

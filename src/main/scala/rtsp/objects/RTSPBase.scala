package rtsp.objects

import rtsp.objects.RTSPWarrior
import rtsp.Constants.BattleC.*
import engine2D.graphics.TextureManager
import engine2D.objects.SpriteObject
import rtsp.battle.*

class RTSPBase(
    engine: engine2D.GameEngine,
    battle: RTSPBattle,
    player: rtsp.Player
) extends RTSPWarrior(
      engine,
      battle,
      player.id,
      BASE_HP,
      BASE_RANGE,
      BASE_ATTACK_DAMAGE,
      BASE_ATTACK_DELAY,
      0f,
      Behavior.basicBehavior(battle),
      SpriteObject(TextureManager.getTexture("base.png"), engine)
    ) {

  override def onDeath(): Unit = {
    battle.addLoser(player.id)
    super.onDeath()
  }
  super.isGrabbable = false

  /** Base is not grabbable */
  override def isGrabbable_=(value: Boolean): Unit = {}

}
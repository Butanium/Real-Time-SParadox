package rtsp.objects

import rtsp.battle.Behavior
import rtsp.battle.RTSPBattle

class RangeWarrior[P <: Projectile](
    engine: engine2D.GameEngine,
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
) extends  RTSPWarrior(engine, battle, team, maxHP, range, attackDamage, attackDelay, speed, behavior, spriteTexture, debug, benched, price, name) {
  // todo
  
}

package rtsp.objects

import rtsp.battle.Behavior
import rtsp.battle.RTSPBattle
import rtsp.objects.Projectile

class RangeWarrior[P <: Projectile](
    engine: engine2D.GameEngine,
    battle: RTSPBattle,
    team: Int,
    maxHP: Int,
    range: Int,
    attackDamage: Int,
    attackDelay: Float,
    speed: Float,
    behavior: Behavior,
    spriteTexture: String,
    val factory: (shooter: RTSPWarrior, target: RTSPWarrior) => P,
    debug: Boolean = false,
    benched: Boolean = false,
    price: Int = 1,
    name: String = "TemplateWarrior"
) extends RTSPWarrior(
      engine,
      battle,
      team,
      maxHP,
      range,
      attackDamage,
      attackDelay,
      speed,
      behavior,
      spriteTexture,
      debug,
      benched,
      price,
      name
    ) {
  override def performAttack(target: RTSPWarrior) = {
    val projectile = factory(this, target)
    projectile.position = position
    engine.spawn(projectile)
  }
}
